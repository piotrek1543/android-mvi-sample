/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.piotrek1543.example.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.piotrek1543.example.todoapp.cache.db.ToDoDatabase
import com.piotrek1543.example.todoapp.cache.source.TasksCachedDataSource
import com.piotrek1543.example.todoapp.data.FakeTasksRemoteDataSource
import com.piotrek1543.example.todoapp.data.TasksDataRepository
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TasksRepository]. This is the mock version, with a
 * [FakeTasksRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: ToDoDatabase? = null
    @Volatile var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?:
                tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        database = Room.databaseBuilder(context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db")
            .build()

        return TasksDataRepository(
                FakeTasksRemoteDataSource,
                TasksCachedDataSource(database!!.taskDao())
        )
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                FakeTasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            tasksRepository = null
        }
    }
}
