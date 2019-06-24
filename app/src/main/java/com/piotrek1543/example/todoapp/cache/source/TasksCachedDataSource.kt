package com.piotrek1543.example.todoapp.cache.source


import com.piotrek1543.example.todoapp.cache.dao.CachedTasksDao
import com.piotrek1543.example.todoapp.cache.mapper.TaskEntityMapper
import com.piotrek1543.example.todoapp.data.Result
import com.piotrek1543.example.todoapp.data.Result.Error
import com.piotrek1543.example.todoapp.data.Result.Success
import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TasksCachedDataSource internal constructor(
        private val cachedTasksDao: CachedTasksDao,
        private val entityMapper: TaskEntityMapper = TaskEntityMapper(), //todo: use DI here
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(cachedTasksDao.getTasks().map { entityMapper.mapFromCached(it) })
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = cachedTasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(entityMapper.mapFromCached(task))
            } else {
                return@withContext Error(Exception("CachedTask not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        cachedTasksDao.insertTask(entityMapper.mapToCached(task))
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        cachedTasksDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the local data source because the {@link TasksDataRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        cachedTasksDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the local data source because the {@link TasksDataRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        cachedTasksDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        cachedTasksDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        cachedTasksDao.deleteTaskById(taskId)
    }
}
