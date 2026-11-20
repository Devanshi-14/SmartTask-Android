package com.demo.smarttask.data.remote

data class TodoDto(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int
)