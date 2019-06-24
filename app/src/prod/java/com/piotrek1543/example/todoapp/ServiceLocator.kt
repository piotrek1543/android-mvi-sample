package com.piotrek1543.example.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.piotrek1543.example.todoapp.cache.db.ToDoDatabase
import com.piotrek1543.example.todoapp.cache.source.TasksCachedDataSource
import com.piotrek1543.example.todoapp.data.TasksDataRepository
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TasksRepository]. This is the prod version, with a
 * the "real" [TasksRemoteDataSource].
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
            TasksRemoteDataSource,
                TasksCachedDataSource(database!!.taskDao())
        )
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
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