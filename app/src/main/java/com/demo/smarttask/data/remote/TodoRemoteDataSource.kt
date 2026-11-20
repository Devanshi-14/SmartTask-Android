package com.demo.smarttask.data.remote

class TodoRemoteDataSource(
    private val apiService: TodoApiService
) {
    suspend fun fetchTodos(): TodoResponse {
        return apiService.getTodos()
    }
    suspend fun addRemoteTodo(todo: AddTodoRequest): TodoDto {
        return apiService.addTodo(todo)
    }
}
//Architecture लक्षात ठेव:
//
//ViewModel → Repository → RemoteDataSource → Retrofit API