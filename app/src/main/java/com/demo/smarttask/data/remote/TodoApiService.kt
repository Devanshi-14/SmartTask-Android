package com.demo.smarttask.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TodoApiService {

    @GET("todos")
    suspend fun getTodos(): TodoResponse
    @POST("todos/add")
    suspend fun addTodo(@Body todo: AddTodoRequest): TodoDto
}