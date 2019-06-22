package com.piotrek1543.example.todoapp.data

import com.google.common.collect.Lists
import com.piotrek1543.example.todoapp.data.Result.Error
import com.piotrek1543.example.todoapp.data.Result.Success
import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.data.source.TasksDataSource
import com.piotrek1543.example.todoapp.remote.mapper.TaskEntityMapper
import com.piotrek1543.example.todoapp.remote.model.TaskModel
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeTasksRemoteDataSource : TasksDataSource {

    private val entityMapper: TaskEntityMapper = TaskEntityMapper() //todo: use DI here

    private var TASKS_SERVICE_DATA: LinkedHashMap<String, TaskModel> = LinkedHashMap()

    override suspend fun getTask(taskId: String): Result<Task> {
        TASKS_SERVICE_DATA[taskId]?.let {
            val data = entityMapper.mapFromRemote(it)
            return Success(data)
        }
        return Error(Exception("Could not find task"))
    }

    override suspend fun getTasks(): Result<List<Task>> {
        val list = TASKS_SERVICE_DATA.values.map { entityMapper.mapFromRemote(it) }
        return Success(Lists.newArrayList(list))
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = entityMapper.mapToRemote(task)
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        TASKS_SERVICE_DATA[task.id] = entityMapper.mapToRemote(completedTask)
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source.
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        TASKS_SERVICE_DATA[task.id] = entityMapper.mapToRemote(activeTask)
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the remote data source.
    }

    override suspend fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, TaskModel>
    }

    override suspend fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }
}
