package com.piotrek1543.example.todoapp.presentation.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.piotrek1543.example.todoapp.Injection
import com.piotrek1543.example.todoapp.presentation.tasks.TasksActionProcessorHolder
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewModel

class ToDoViewModelFactory private constructor(
        private val applicationContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == TasksViewModel::class.java) {
            return TasksViewModel(
                    TasksActionProcessorHolder(
                            Injection.provideTasksRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }
        throw IllegalArgumentException("unknown model class $modelClass")
    }

    companion object : SingletonHolderSingleArg<ToDoViewModelFactory, Context>(::ToDoViewModelFactory)
}
