package com.demo.smarttask.repository

import android.util.Log
import com.demo.smarttask.data.Task
import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.data.local.TaskDao
import com.demo.smarttask.data.local.toEntity
import com.demo.smarttask.data.local.toTask
import com.demo.smarttask.data.remote.AddTodoRequest
import com.demo.smarttask.data.remote.TodoDto
import com.demo.smarttask.data.remote.TodoRemoteDataSource
import com.demo.smarttask.data.remote.TodoResponse
import com.demo.smarttask.data.remote.toTaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TaskRepository@Inject constructor(
    private val taskDao: TaskDao,
    private val taskRemoteDataSource: TodoRemoteDataSource
) {

    // Observe all tasks from Room Database
    val tasks: Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }

    // Fetch remote data
    suspend fun fetchRemoteTodos(): TodoResponse {
        return taskRemoteDataSource.fetchTodos()
    }

    // Add task to Remote
    suspend fun addRemoteTodo(todo: String, userId: Int): TodoDto {
        val request = AddTodoRequest(
            todo = todo,
            completed = false,
            userId = userId
        )
        return taskRemoteDataSource.addRemoteTodo(request)
    }

    // save remote data to database
    suspend fun saveRemoteTodoData(todos: List<TodoDto>) {
        todos.forEach { dto ->
            val existingTask = taskDao.getTaskByRemoteId(dto.id)
            val entity = dto.toTaskEntity().copy(id = existingTask?.id ?: 0)
            Log.d("SMART_TASK_API", "existingTask: ${existingTask?.remoteId} == entity: ${entity?.remoteId}")

            if (existingTask == null) {
                taskDao.insertTask(entity)
            } else {
                taskDao.updateTask(entity)
            }
        }
    }
    // Sync tasks to server
    suspend fun syncPendingTasks(userId: Int){
        val pendingTasks = taskDao.getUnSyncedTasks()
        pendingTasks.forEach { task ->
            val request = AddTodoRequest(
                todo = task.title,
                completed = task.isCompleted,
                userId = userId
            )
            val response = taskRemoteDataSource.addRemoteTodo(request)
            taskDao.updateTask(task.copy(isSynced = true, remoteId = response.id))
        }
    }

    // Add new task
    suspend fun addTask(
        title: String,
        description: String,
        priority: TaskPriority,
        dueDate: LocalDate?
    ) {
        val newTitle = title.trim()
        val newDescription = description.trim()

        if (newTitle.isNotEmpty()) {
            val task = Task(
                id = 0,
                title = newTitle,
                description = newDescription,
                isCompleted = false,
                priority = priority,
                dueDate = dueDate
            )

            taskDao.insertTask(task.toEntity())
        }
    }

    // update task completion
    suspend fun updateTaskCompletion(
        id: Int,
        isCompleted: Boolean
    ) {
        val existingTask = taskDao.getTaskById(id) ?: return

        taskDao.updateTask(
            existingTask.copy(isCompleted = isCompleted)
        )
    }

    suspend fun updateTask(
        id: Int,
        updated: Task
    ) {
        val existingTask = taskDao.getTaskById(id) ?: return

        val updatedEntity = updated.toEntity().copy(
            id = existingTask.id,
            remoteId = existingTask.remoteId,
            isSynced = existingTask.isSynced
        )
        taskDao.updateTask(
            updatedEntity
        )
    }

    suspend fun deleteTask(id: Int) {
        val existingTask = taskDao.getTaskById(id) ?: return

        taskDao.deleteTask(existingTask)
    }
}
