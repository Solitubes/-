package com.example.todoapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.todoapp.data.TodoItem
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TodoNotificationWorker.CHANNEL_ID,
                "待办事项提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "待办事项到期提醒"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun scheduleNotification(todo: TodoItem) {
        val workManager = WorkManager.getInstance(context)
        
        // 计算延迟时间（截止时间前1小时）
        val notificationTime = todo.dueDate.minusHours(1)
        val now = LocalDateTime.now()
        
        if (notificationTime.isAfter(now)) {
            val delay = Duration.between(now, notificationTime)
            
            val notificationRequest = OneTimeWorkRequestBuilder<TodoNotificationWorker>()
                .setInitialDelay(delay.toMinutes(), TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .addTag("todo_${todo.id}")
                .build()
            
            workManager.enqueueUniqueWork(
                "todo_notification_${todo.id}",
                ExistingWorkPolicy.REPLACE,
                notificationRequest
            )
        }
    }
    
    fun cancelNotification(todoId: Long) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("todo_notification_$todoId")
    }
}
