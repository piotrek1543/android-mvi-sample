package com.piotrek1543.example.todoapp.ui.tasks


import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.PopupMenu
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.presentation.base.BaseView
import com.piotrek1543.example.todoapp.presentation.tasks.TasksFilterType
import com.piotrek1543.example.todoapp.presentation.tasks.TasksIntent
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewModel
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewState
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewState.UiNotification.*
import com.piotrek1543.example.todoapp.presentation.util.ToDoViewModelFactory
import com.piotrek1543.example.todoapp.ui.addedittask.AddEditTaskActivity
import com.piotrek1543.example.todoapp.ui.taskdetail.TaskDetailActivity
import com.piotrek1543.example.todoapp.ui.view.ScrollChildSwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.tasks_act.*
import java.util.*

/**
 * This fragment hold the UI for the list of tasks you can check off.
 */
//TODO: Cleanup code
class TasksFragment : androidx.fragment.app.Fragment(), BaseView<TasksIntent, TasksViewState> {

    //FIXME: Kotlin extensions does not work properly
    private lateinit var noTasksView: View
    private lateinit var noTaskIcon: ImageView
    private lateinit var noTaskMainView: TextView
    private lateinit var noTaskAddView: TextView
    private lateinit var tasksView: LinearLayout
    private lateinit var filteringLabelView: TextView
    private lateinit var swipeRefreshLayout: ScrollChildSwipeRefreshLayout

    private lateinit var listAdapter: TasksAdapter

    private val refreshIntentPublisher = PublishSubject.create<TasksIntent.RefreshIntent>()

    private val clearCompletedTaskIntentPublisher =
            PublishSubject.create<TasksIntent.ClearCompletedTasksIntent>()

    private val changeFilterIntentPublisher = PublishSubject.create<TasksIntent.ChangeFilterIntent>()

    // Used to manage the data flow lifecycle and avoid memory leak.
    private val disposables = CompositeDisposable()

    private val viewModel: TasksViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
                .of(this, ToDoViewModelFactory.getInstance(context!!))
                .get(TasksViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = TasksAdapter(ArrayList(0))
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        bind()
    }

    /**
     * Connect the [BaseView] with the [BaseViewModel]
     * We subscribe to the [BaseViewModel] before passing it the [BaseView]'s [BaseIntent]s.
     * If we were to pass [BaseIntent]s to the [BaseViewModel] before listening to it,
     * emitted [BaseViewState]s could be lost
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())

        disposables.add(
                listAdapter.taskClickObservable.subscribe { task -> showTaskDetailsUi(task.id) })
    }

    override fun onResume() {
        super.onResume()
        // conflicting with the initial intent but needed when coming back from the
        // AddEditTask activity to refresh the list.
        refreshIntentPublisher.onNext(TasksIntent.RefreshIntent(false))
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }


    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            showSuccessfullySavedMessage()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.tasks_frag, container, false)

        // Set up tasks view
        val listView = root.findViewById<ListView>(R.id.tasks_list)
        listView.adapter = listAdapter
        filteringLabelView = root.findViewById(R.id.filteringLabel)
        tasksView = root.findViewById(R.id.tasksLL)

        // Set up  no tasks view
        noTasksView = root.findViewById(R.id.noTasks)
        noTaskIcon = root.findViewById(R.id.noTasksIcon)
        noTaskMainView = root.findViewById(R.id.noTasksMain)
        noTaskAddView = root.findViewById(R.id.noTasksAdd)
        noTaskAddView.setOnClickListener { showAddTask() }

        // Set up floating action button
        val fab = activity!!.findViewById<FloatingActionButton>(R.id.fab_add_task)

        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener { showAddTask() }

        // Set up progress indicator
        swipeRefreshLayout = root.findViewById(R.id.refresh_layout)
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(activity!!, R.color.colorPrimary),
                ContextCompat.getColor(activity!!, R.color.colorAccent),
                ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)
        )
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView)

        setHasOptionsMenu(true)

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_clear ->
                clearCompletedTaskIntentPublisher.onNext(TasksIntent.ClearCompletedTasksIntent)
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> refreshIntentPublisher.onNext(TasksIntent.RefreshIntent(true))
        }
        return true
    }

    override fun onCreateOptionsMenu(
            menu: Menu?,
            inflater: MenuInflater?
    ) {
        inflater!!.inflate(R.menu.tasks_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun intents(): Observable<TasksIntent> {
        return Observable.merge(
                initialIntent(),
                refreshIntent(),
                adapterIntents(),
                clearCompletedTaskIntent()
        )
                .mergeWith(
                        changeFilterIntent()
                )
    }

    override fun render(state: TasksViewState) {
        swipeRefreshLayout.isRefreshing = state.isLoading
        if (state.error != null) {
            showLoadingTasksError()
            return
        }

        when (state.uiNotification) {
            TASK_COMPLETE -> showMessage(getString(R.string.task_marked_complete))
            TASK_ACTIVATED -> showMessage(getString(R.string.task_marked_active))
            COMPLETE_TASKS_CLEARED -> showMessage(getString(R.string.completed_tasks_cleared))
            null -> {
            }
        }

        if (state.tasks.isEmpty()) {
            when (state.tasksFilterType) {
                TasksFilterType.ACTIVE_TASKS -> showNoActiveTasks()
                TasksFilterType.COMPLETED_TASKS -> showNoCompletedTasks()
                else -> showNoTasks()
            }
        } else {
            listAdapter.replaceData(state.tasks)

            tasksView.visibility = View.VISIBLE
            noTasksView.visibility = View.GONE

            when (state.tasksFilterType) {
                TasksFilterType.ACTIVE_TASKS -> showActiveFilterLabel()
                TasksFilterType.COMPLETED_TASKS -> showCompletedFilterLabel()
                else -> showAllFilterLabel()
            }
        }
    }

    private fun showFilteringPopUpMenu() {
        val popup = PopupMenu(context!!, activity!!.findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.active -> changeFilterIntentPublisher.onNext(
                        TasksIntent.ChangeFilterIntent(TasksFilterType.ACTIVE_TASKS)
                )
                R.id.completed -> changeFilterIntentPublisher.onNext(
                        TasksIntent.ChangeFilterIntent(TasksFilterType.COMPLETED_TASKS)
                )
                else -> changeFilterIntentPublisher.onNext(TasksIntent.ChangeFilterIntent(TasksFilterType.ALL_TASKS))
            }
            true
        }

        popup.show()
    }

    private fun showMessage(message: String) {
        val view = view ?: return
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .show()
    }

    /**
     * The initial Intent the [BaseView] emit to convey to the [BaseViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [BaseViewModel] might need
     * to render the initial [BaseViewState] (e.g. the task id to load).
     */
    private fun initialIntent(): Observable<TasksIntent.InitialIntent> {
        return Observable.just(TasksIntent.InitialIntent)
    }

    private fun refreshIntent(): Observable<TasksIntent.RefreshIntent> {
        return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                .map { TasksIntent.RefreshIntent(false) }
                .mergeWith(refreshIntentPublisher)
    }

    private fun clearCompletedTaskIntent(): Observable<TasksIntent.ClearCompletedTasksIntent> {
        return clearCompletedTaskIntentPublisher
    }

    private fun changeFilterIntent(): Observable<TasksIntent.ChangeFilterIntent> {
        return changeFilterIntentPublisher
    }

    private fun adapterIntents(): Observable<TasksIntent> {
        return listAdapter.taskToggleObservable.map { task ->
            if (!task.completed) {
                TasksIntent.CompleteTaskIntent(task)
            } else {
                TasksIntent.ActivateTaskIntent(task)
            }
        }
    }

    private fun showNoActiveTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp, false
        )
    }

    private fun showNoTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp, true
        )
    }

    private fun showNoCompletedTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp, false
        )
    }

    private fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message))
    }

    private fun showNoTasksViews(
            mainText: String,
            iconRes: Int,
            showAddView: Boolean
    ) {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE

        noTaskMainView.text = mainText
        noTaskIcon.setImageDrawable(ContextCompat.getDrawable(context!!, iconRes))
        noTaskAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    private fun showActiveFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_active)
    }

    private fun showCompletedFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_completed)
    }

    private fun showAllFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_all)
    }

    private fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    private fun showTaskDetailsUi(taskId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some MviIntent stubbing.
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    private fun showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    companion object {
        operator fun invoke(): TasksFragment = TasksFragment()
    }

}