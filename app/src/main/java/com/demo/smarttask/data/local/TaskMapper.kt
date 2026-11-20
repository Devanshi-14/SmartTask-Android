package com.demo.smarttask.data.local

import com.demo.smarttask.data.Task
import com.demo.smarttask.data.TaskPriority
import java.time.LocalDate

// Database Entity → UI Model
fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = TaskPriority.valueOf(priority),
        dueDate = dueDate?.let { LocalDate.parse(it) }
    )
}

// UI Model → Database Entity
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        remoteId = null,
        isSynced = false,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = priority.name,
        dueDate = dueDate?.toString()
    )
}
