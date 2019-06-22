package com.piotrek1543.example.todoapp

import android.app.Application
import timber.log.Timber


class TodoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Logger init
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.i("%s %d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

}