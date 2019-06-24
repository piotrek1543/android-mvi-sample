package com.piotrek1543.example.todoapp.remote.model

import java.util.*

/**
 * Representation for a [TaskModel] fetched from the API
 */
data class TaskModel @JvmOverloads constructor(
        var title: String = "",
        var description: String = "",
        var isCompleted: Boolean = false,
        var id: String = UUID.randomUUID().toString()
) {

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description


    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}