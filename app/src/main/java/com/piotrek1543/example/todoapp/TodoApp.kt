package com.piotrek1543.example.todoapp

import android.app.Application
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import timber.log.Timber


/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class TodoApp : Application() {

    // Depends on the flavor,
    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()

        // Logger init
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.i("%s %d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

}