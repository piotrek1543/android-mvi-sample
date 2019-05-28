package com.piotrek1543.example.todoapp.presentation.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context

class ToDoViewModelFactory private constructor(
        private val applicationContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
/*        if (modelClass == StatisticsViewModel::class.java) {
            return StatisticsViewModel(
                    StatisticsActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }*/
/*        if (modelClass == TasksViewModel::class.java) {
            return TasksViewModel(
                    TasksActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }*/
/*        if (modelClass == AddEditTaskViewModel::class.java) {
            return AddEditTaskViewModel(
                    AddEditTaskActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }*/
/*        if (modelClass == TaskDetailViewModel::class.java) {
            return TaskDetailViewModel(
                    TaskDetailActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }*/
        throw IllegalArgumentException("unknown model class $modelClass")
    }

    companion object : SingletonHolderSingleArg<ToDoViewModelFactory, Context>(::ToDoViewModelFactory)
}
