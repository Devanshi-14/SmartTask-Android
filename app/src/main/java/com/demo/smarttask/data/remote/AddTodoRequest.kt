package com.demo.smarttask.data.remote

data class AddTodoRequest(
    val todo: String,
    val completed: Boolean,
    val userId: Int
)
