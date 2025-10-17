package com.example.todoapp.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.R
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.TodoItem
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class TodoNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val database = TodoDatabase.getDatabase(applicationContext)
            val todoDao = database.todoDao()
            
            // 获取即将到期的待办事项（未来1小时内）
            val now = LocalDateTime.now()
            val oneHourLater = now.plusHours(1)
            
            val upcomingTodos = todoDao.getTodosDueBy(oneHourLater)
                .filter { it.dueDate.isAfter(now) && !it.isCompleted }
            
            for (todo in upcomingTodos) {
                sendNotification(todo)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun sendNotification(todo: TodoItem) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("待办事项提醒")
            .setContentText("${todo.title} 即将到期")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(todo.id.toInt(), notification)
    }
    
    companion object {
        const val CHANNEL_ID = "todo_notifications"
    }
}
