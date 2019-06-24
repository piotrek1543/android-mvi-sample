package com.piotrek1543.example.todoapp.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.piotrek1543.example.todoapp.cache.dao.CachedTasksDao
import com.piotrek1543.example.todoapp.cache.model.CachedTask

/**
 * The Room Database that contains the CachedTask table.
 */
@Database(entities = [CachedTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun taskDao(): CachedTasksDao
}