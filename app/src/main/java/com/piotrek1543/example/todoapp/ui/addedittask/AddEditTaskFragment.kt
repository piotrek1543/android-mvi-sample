package com.piotrek1543.example.todoapp.ui.addedittask

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskIntent
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskViewModel
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskViewState
import com.piotrek1543.example.todoapp.presentation.base.BaseView
import com.piotrek1543.example.todoapp.presentation.util.ToDoViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * Fragment for adding/editing tasks.
 */
class AddEditTaskFragment : Fragment(), BaseView<AddEditTaskIntent, AddEditTaskViewState> {

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var fab: FloatingActionButton
    // Used to manage the data flow lifecycle and avoid memory leak.
    private val disposables = CompositeDisposable()
    private val viewModel: AddEditTaskViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
                .of(this, ToDoViewModelFactory.getInstance(context!!))
                .get(AddEditTaskViewModel::class.java)
    }

    private val argumentTaskId: String? get() = arguments?.getString(ARGUMENT_EDIT_TASK_ID)


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.addtask_frag, container, false)
                .also {
                    title = it.findViewById(R.id.add_task_title)
                    description = it.findViewById(R.id.add_task_description)
                    setHasOptionsMenu(true)
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.addtask_fragment_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = activity!!.findViewById(R.id.fab_edit_task_done)
        fab.setImageResource(R.drawable.ic_done)

        bind()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    /**
     * Connect the [MviView] with the [MviViewModel]
     * We subscribe to the [MviViewModel] before passing it the [MviView]'s [MviIntent]s.
     * If we were to pass [MviIntent]s to the [MviViewModel] before listening to it,
     * emitted [MviViewState]s could be lost
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<AddEditTaskIntent> {
        return Observable.merge(initialIntent(), saveTaskIntent())
    }

    /**
     * The initial Intent the [MviView] emit to convey to the [MviViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [MviViewModel] might need
     * to render the initial [MviViewState] (e.g. the task id to load).
     */
    private fun initialIntent(): Observable<AddEditTaskIntent.InitialIntent> {
        return Observable.just(AddEditTaskIntent.InitialIntent(argumentTaskId))
    }

    private fun saveTaskIntent(): Observable<AddEditTaskIntent.SaveTask> {
        // Wrap the FAB click events into a SaveTaskIntent and set required information
        return RxView.clicks(fab).map {
            AddEditTaskIntent.SaveTask(argumentTaskId, title.text.toString(), description.text.toString())
        }
    }

    override fun render(state: AddEditTaskViewState) {
        when {
            state.isSaved -> {
                showTasksList()
                return
            }
            state.isEmpty -> showEmptyTaskError()
        }
        if (state.title.isNotEmpty()) {
            setTitle(state.title)
        }
        if (state.description.isNotEmpty()) {
            setDescription(state.description)
        }
    }

    private fun showEmptyTaskError() {
        Snackbar.make(title, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show()
    }

    private fun showTasksList() {
        activity!!.setResult(Activity.RESULT_OK)
        activity!!.finish()
    }

    private fun setTitle(title: String) {
        this.title.text = title
    }

    private fun setDescription(description: String) {
        this.description.text = description
    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        operator fun invoke(): AddEditTaskFragment = AddEditTaskFragment()

    }

}