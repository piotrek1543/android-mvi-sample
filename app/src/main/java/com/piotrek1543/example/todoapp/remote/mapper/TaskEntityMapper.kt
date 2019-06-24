package com.piotrek1543.example.todoapp.remote.mapper

import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.remote.model.TaskModel


/**
 * Map a [TaskModel] to and from a [Task] instance when data is moving between
 * this later and the Data layer
 */
open class TaskEntityMapper : EntityMapper<TaskModel, Task> {

    /**
     * Map an instance of a [TaskModel] to a [Task] model
     */
    override fun mapFromRemote(type: TaskModel): Task = Task(
            id = type.id,
            title = type.title,
            description = type.description,
            isCompleted = type.isCompleted
    )
}