package com.piotrek1543.example.todoapp.data.model

import java.util.*

data class Task(
        val id: String = UUID.randomUUID().toString(),
        val title: String?,
        val description: String?,
        val completed: Boolean = false
)