package com.demo.smarttask.taskreminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.demo.smarttask.notification.NotificationHelper
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val taskTitle = inputData.getString("taskTitle") ?: "Task Reminder"
        val taskId = inputData.getInt("taskId", 1)

        NotificationHelper.showTaskReminder(
            context = applicationContext,
            taskTitle = taskTitle,
            notificationId = taskId
        )

        return Result.success()
    }

}
//object TaskReminderScheduler {
//
//    fun scheduleReminder(context: Context) {
//
//        val workRequest =
//            OneTimeWorkRequestBuilder<TaskReminderWorker>()
//                .setInitialDelay(10, TimeUnit.SECONDS)
//                .build()
//
//        WorkManager.getInstance(context)
//            .enqueue(workRequest)
//    }
//}


object TaskReminderScheduler {

    fun scheduleReminder(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: LocalDate
    ) {
        val reminderDateTime = dueDate
            .atTime(9, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()

        val delay = reminderDateTime.toEpochMilli() -
                System.currentTimeMillis()
//        val delay = TimeUnit.SECONDS.toMillis(10)

        if (delay <= 0) {
            cancelReminder(context, taskId)
            return
        }

        val inputData = workDataOf(
            "taskId" to taskId,
            "taskTitle" to taskTitle
        )

        val workRequest =
            OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "task_reminder_$taskId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
    fun cancelReminder(
        context: Context,
        taskId: Int
    ) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("task_reminder_$taskId")
    }
}
