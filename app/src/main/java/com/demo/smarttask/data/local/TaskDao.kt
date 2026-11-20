package com.demo.smarttask.data.local

import androidx.compose.runtime.snapshots.SnapshotId
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Insert
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getTaskByRemoteId(remoteId: Int): TaskEntity?

     @Query("SELECT * FROM tasks WHERE isCompleted = 0")
     suspend fun getUnSyncedTasks(): List<TaskEntity>
}