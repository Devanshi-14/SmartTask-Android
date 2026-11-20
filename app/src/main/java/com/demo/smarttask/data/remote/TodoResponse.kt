package com.demo.smarttask.data.remote

data class TodoResponse(
    val todos: List<TodoDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)