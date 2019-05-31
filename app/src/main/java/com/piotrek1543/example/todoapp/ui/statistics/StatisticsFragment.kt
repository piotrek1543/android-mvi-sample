package com.piotrek1543.example.todoapp.ui.statistics

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.presentation.base.BaseIntent
import com.piotrek1543.example.todoapp.presentation.base.BaseView
import com.piotrek1543.example.todoapp.presentation.base.BaseViewModel
import com.piotrek1543.example.todoapp.presentation.base.BaseViewState
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsIntent
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsViewModel
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsViewState
import com.piotrek1543.example.todoapp.presentation.util.ToDoViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * Shows statistics for the app.
 */
class StatisticsFragment : Fragment(), BaseView<StatisticsIntent, StatisticsViewState> {
    private lateinit var statisticsTV: TextView
    // Used to manage the data flow lifecycle and avoid memory leak.
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val viewModel: StatisticsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders
                .of(this, ToDoViewModelFactory.getInstance(context!!))
                .get(StatisticsViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.statistics_frag, container, false)
                .also { statisticsTV = it.findViewById(R.id.statistics) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    /**
     * Connect the [BaseView] with the [BaseViewModel].
     * We subscribe to the [BaseViewModel] before passing it the [BaseView]'s [BaseIntent]s.
     * If we were to pass [BaseIntent]s to the [BaseViewModel] before listening to it,
     * emitted [BaseViewState]s could be lost.
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(
                viewModel.states().subscribe { this.render(it) }
        )
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun intents(): Observable<StatisticsIntent> = initialIntent()

    /**
     * The initial Intent the [BaseView] emit to convey to the [BaseViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [BaseViewModel] might need
     * to render the initial [BaseViewState] (e.g. the task id to load).
     */
    private fun initialIntent(): Observable<StatisticsIntent> {
        return Observable.just(StatisticsIntent.InitialIntent)
    }

    override fun render(state: StatisticsViewState) {
        if (state.isLoading) statisticsTV.text = getString(R.string.loading)
        if (state.error != null) {
            statisticsTV.text = resources.getString(R.string.statistics_error)
        }

        if (state.error == null && !state.isLoading) {
            showStatistics(state.activeCount, state.completedCount)
        }
    }

    private fun showStatistics(numberOfActiveTasks: Int, numberOfCompletedTasks: Int) {
        if (numberOfCompletedTasks == 0 && numberOfActiveTasks == 0) {
            statisticsTV.text = resources.getString(R.string.statistics_no_tasks)
        } else {
            val displayString = (resources.getString(R.string.statistics_active_tasks)
                    + " "
                    + numberOfActiveTasks
                    + "\n"
                    + resources.getString(R.string.statistics_completed_tasks)
                    + " "
                    + numberOfCompletedTasks)
            statisticsTV.text = displayString
        }
    }

    companion object {
        operator fun invoke(): StatisticsFragment = StatisticsFragment()
    }
}