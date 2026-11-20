package com.demo.smarttask.data.remote

import com.demo.smarttask.data.Task
import com.demo.smarttask.data.local.TaskEntity

fun TodoDto.toTask(): Task {
    return Task(
        id =0,
        title = todo,
        description = ""
    )
}

fun TodoDto.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = 0,
        remoteId = id,
        isSynced = true,
        title = todo,
        description = "",
        isCompleted = completed,
        priority = "MEDIUM",
        dueDate = null
    )
}