package com.piotrek1543.example.todoapp.cache.mapper

import com.piotrek1543.example.todoapp.cache.model.CachedTask
import com.piotrek1543.example.todoapp.data.model.Task

/**
 * Map a [CachedTask] instance to and from a [Task] instance when data is moving between
 * this later and the Data layer
 */
open class TaskEntityMapper :
        EntityMapper<CachedTask, Task> {

    /**
     * Map a [Task] instance to a [CachedTask] instance
     */
    override fun mapToCached(type: Task): CachedTask = CachedTask(
            id = type.id,
            title = type.title,
            description = type.description,
            isCompleted = type.isCompleted
    )

    /**
     * Map a [CachedTask] instance to a [Task] instance
     */
    override fun mapFromCached(type: CachedTask): Task = Task(
            id = type.id,
            title = type.title,
            description = type.description,
            isCompleted = type.isCompleted
    )
}