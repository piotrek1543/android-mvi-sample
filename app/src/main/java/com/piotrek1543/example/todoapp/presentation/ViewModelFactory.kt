package com.piotrek1543.example.todoapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.presentation.addedittask.AddEditTaskViewModel
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsViewModel
import com.piotrek1543.example.todoapp.presentation.taskdetail.TaskDetailViewModel
import com.piotrek1543.example.todoapp.presentation.tasks.TasksViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory constructor(
        private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(TasksViewModel::class.java) ->
                        TasksViewModel(tasksRepository)
                    isAssignableFrom(AddEditTaskViewModel::class.java) ->
                        AddEditTaskViewModel(tasksRepository)
                    isAssignableFrom(TaskDetailViewModel::class.java) ->
                        TaskDetailViewModel(tasksRepository)
                    isAssignableFrom(StatisticsViewModel::class.java) ->
                        StatisticsViewModel(tasksRepository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T
}
