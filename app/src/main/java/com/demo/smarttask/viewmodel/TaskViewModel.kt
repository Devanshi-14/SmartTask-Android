package com.demo.smarttask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.smarttask.data.Task
import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.data.remote.TodoResponse
import com.demo.smarttask.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.util.Log
import com.demo.smarttask.data.remote.TodoDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    // Remote Data
    // API Success State
    private val _apiSuccess = MutableStateFlow<TodoResponse?>(null)
    val apiSuccess: StateFlow<TodoResponse?> = _apiSuccess

    // Loading State
    private val _apiLoading = MutableStateFlow(false)
    val apiLoading: StateFlow<Boolean> = _apiLoading

    // Error State
    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError

    //  Result State
    private val _syncLoading = MutableStateFlow(false)
    val syncLoading: StateFlow<Boolean> = _syncLoading

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError

    // Fetch Todos from API
    fun fetchTodos() {
        viewModelScope.launch {
            _apiLoading.value = true
            _apiError.value = null

            try {
                val response = repository.fetchRemoteTodos()

                _apiSuccess.value = response
                repository.saveRemoteTodoData(response.todos)
                Log.d("SMART_TASK_API", "API_SUCCESS: ${response.todos}")

            } catch (e: Exception) {
                _apiError.value = e.message ?: "Something went wrong"

            } finally {
                _apiLoading.value = false
            }
        }
    }
    // sync all task to server
    fun syncPendingTasks() {
        viewModelScope.launch {
            _syncLoading.value = true
            _syncError.value = null
            _syncMessage.value = null

            try {
                repository.syncPendingTasks(userId = 1)
                _syncMessage.value = "Tasks synced successfully"
            } catch (e: Exception) {
                _syncError.value = e.message ?: "Sync failed"
            } finally {
                _syncLoading.value = false
            }
        }
    }

    // Database offline data
    val tasks: StateFlow<List<Task>> =
        repository.tasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTask(
        title: String,
        description: String,
        priority: TaskPriority,
        dueDate: LocalDate?
    ) {
        viewModelScope.launch {
            repository.addTask(title, description, priority, dueDate)
        }
    }

    fun updateTaskCompletion(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(id, isCompleted)
        }
    }

    // update record
    fun updateTask(id: Int, updated: Task) {
        viewModelScope.launch {
            repository.updateTask(id, updated)
        }
    }

    // Delete Record
    fun deleteTask(id: Int) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }
}