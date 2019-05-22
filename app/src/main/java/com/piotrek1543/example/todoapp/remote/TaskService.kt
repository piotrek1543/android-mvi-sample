package com.piotrek1543.example.todoapp.remote

import com.piotrek1543.example.todoapp.remote.model.Task
import io.reactivex.Observable
import retrofit2.http.GET


interface TaskService {

    /**
     * Get a Map of (read only) tasks from demo Firebase sample.
     */
    @GET("tasks.json")
    fun getTasks(): Observable<Map<String, Task>>
}