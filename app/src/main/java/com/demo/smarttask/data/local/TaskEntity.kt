package com.demo.smarttask.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val remoteId: Int? = null,

    val isSynced: Boolean = false,
    val title: String,

    val description: String,

    val isCompleted: Boolean,

    val priority: String,

    val dueDate: String?
)