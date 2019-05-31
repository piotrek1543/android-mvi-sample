package com.piotrek1543.example.todoapp.presentation.statistics

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.data.schedulers.BaseSchedulerProvider
import com.piotrek1543.example.todoapp.presentation.util.flatMapIterable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * Contains and executes the business logic for all emitted [MviAction]
 * and returns one unique [Observable] of [MviResult].
 *
 *
 * This could have been included inside the [MviViewModel]
 * but was separated to ease maintenance, as the [MviViewModel] was getting too big.
 */
class StatisticsActionProcessorHolder(
        private val tasksRepository: TasksRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadStatisticsProcessor =
            ObservableTransformer<StatisticsAction.LoadStatisticsAction, StatisticsResult.LoadStatisticsResult> { actions ->
                actions.flatMap {
                    tasksRepository.getTasks()
                            // Transform one event of a List<Task> to an observable<Task>.
                            .flatMapIterable()
                            // Count all active and completed tasks and wrap the result into a Pair.
                            .publish<StatisticsResult.LoadStatisticsResult.Success> { shared ->
                                Single.zip<Int, Int, StatisticsResult.LoadStatisticsResult.Success>(
                                        shared.filter(Task::active).count().map(Long::toInt),
                                        shared.filter(Task::completed).count().map(Long::toInt),
                                        BiFunction { activeCount, completedCount ->
                                            StatisticsResult.LoadStatisticsResult.Success(activeCount, completedCount)
                                        }
                                ).toObservable()
                            }
                            .cast(StatisticsResult.LoadStatisticsResult::class.java)
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(StatisticsResult.LoadStatisticsResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(StatisticsResult.LoadStatisticsResult.InFlight)
                }
            }

    /**
     * Splits the [Observable] to match each type of [MviAction] to its corresponding business logic
     * processor. Each processor takes a defined [MviAction], returns a defined [MviResult].
     * The global actionProcessor then merges all [Observable] back to one unique [Observable].
     *
     * The splitting is done using [Observable.publish] which allows almost anything
     * on the passed [Observable] as long as one and only one [Observable] is returned.
     *
     * An security layer is also added for unhandled [MviAction] to allow early crash
     * at runtime to easy the maintenance.
     */
    var actionProcessor =
            ObservableTransformer<StatisticsAction, StatisticsResult> { actions ->
                actions.publish { shared ->
                    // Match LoadStatisticsResult to loadStatisticsProcessor
                    shared.ofType(StatisticsAction.LoadStatisticsAction::class.java).compose(loadStatisticsProcessor)
                            .cast(StatisticsResult::class.java)
                            .mergeWith(
                                    // Error for not implemented actions
                                    shared.filter { v -> v !is StatisticsAction.LoadStatisticsAction }
                                            .flatMap { w ->
                                                Observable.error<StatisticsResult>(
                                                        IllegalArgumentException("Unknown Action type: $w"))
                                            })
                }
            }
}
