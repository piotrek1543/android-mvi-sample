package com.piotrek1543.example.todoapp.presentation.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.piotrek1543.example.todoapp.Injection
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskActionProcessorHolder
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskViewModel
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsActionProcessorHolder
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsViewModel
import com.piotrek1543.example.todoapp.presentation.taskdetail.TaskDetailActionProcessorHolder
import com.piotrek1543.example.todoapp.presentation.taskdetail.TaskDetailViewModel
import com.piotrek1543.example.todoapp.presentation.tasks.TasksActionProcessorHolder
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewModel

class ToDoViewModelFactory private constructor(private val applicationContext: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == StatisticsViewModel::class.java) {
            return StatisticsViewModel(
                    StatisticsActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }
        if (modelClass == TasksViewModel::class.java) {
            return TasksViewModel(
                    TasksActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }
        if (modelClass == AddEditTaskViewModel::class.java) {
            return AddEditTaskViewModel(
                    AddEditTaskActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }
        if (modelClass == TaskDetailViewModel::class.java) {
            return TaskDetailViewModel(
                    TaskDetailActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }
        throw IllegalArgumentException("unknown model class $modelClass")
    }

    companion object : SingletonHolderSingleArg<ToDoViewModelFactory, Context>(::ToDoViewModelFactory)
}

