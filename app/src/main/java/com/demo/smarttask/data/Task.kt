package com.demo.smarttask.data

import java.time.LocalDate

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: LocalDate? = null
)