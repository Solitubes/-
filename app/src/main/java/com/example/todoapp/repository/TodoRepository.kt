package com.example.todoapp.repository

import android.content.Context
import com.example.todoapp.data.TodoDao
import com.example.todoapp.data.TodoItem
import com.example.todoapp.data.RepeatType
import com.example.todoapp.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val context: Context
) {
    private val notificationHelper = NotificationHelper(context)
    fun getAllTodos(): Flow<List<TodoItem>> = todoDao.getAllTodos()
    
    fun getActiveTodos(): Flow<List<TodoItem>> = todoDao.getActiveTodos()
    
    fun getCompletedTodos(): Flow<List<TodoItem>> = todoDao.getCompletedTodos()
    
    suspend fun getTodoById(id: Long): TodoItem? = todoDao.getTodoById(id)
    
    suspend fun insertTodo(todo: TodoItem): Long {
        val id = todoDao.insertTodo(todo)
        val todoWithId = todo.copy(id = id)
        
        // 安排通知
        notificationHelper.scheduleNotification(todoWithId)
        
        // 如果是重复任务，创建下一个实例
        if (todo.repeatType != RepeatType.ONCE) {
            createNextRecurringTodo(todoWithId)
        }
        return id
    }
    
    suspend fun updateTodo(todo: TodoItem) {
        todoDao.updateTodo(todo)
        // 重新安排通知
        notificationHelper.cancelNotification(todo.id)
        notificationHelper.scheduleNotification(todo)
    }
    
    suspend fun deleteTodo(todo: TodoItem) {
        todoDao.deleteTodo(todo)
        // 取消通知
        notificationHelper.cancelNotification(todo.id)
    }
    
    suspend fun toggleTodoCompletion(todo: TodoItem) {
        val updatedTodo = todo.copy(
            isCompleted = !todo.isCompleted,
            lastModified = LocalDateTime.now()
        )
        todoDao.updateTodo(updatedTodo)
        
        // 如果完成了一个重复任务，创建下一个实例
        if (updatedTodo.isCompleted && updatedTodo.repeatType != RepeatType.ONCE) {
            createNextRecurringTodo(updatedTodo)
        }
        
        // 如果任务完成，取消通知；如果取消完成，重新安排通知
        if (updatedTodo.isCompleted) {
            notificationHelper.cancelNotification(updatedTodo.id)
        } else {
            notificationHelper.scheduleNotification(updatedTodo)
        }
    }
    
    suspend fun getTodosDueBy(date: LocalDateTime): List<TodoItem> {
        return todoDao.getTodosDueBy(date)
    }
    
    private suspend fun createNextRecurringTodo(completedTodo: TodoItem) {
        val nextDueDate = when (completedTodo.repeatType) {
            RepeatType.DAILY -> completedTodo.dueDate.plusDays(1)
            RepeatType.WEEKLY -> completedTodo.dueDate.plusWeeks(1)
            RepeatType.ONCE -> return
        }
        
        val nextTodo = completedTodo.copy(
            id = 0, // 新ID将由数据库生成
            dueDate = nextDueDate,
            isCompleted = false,
            createdAt = LocalDateTime.now(),
            lastModified = LocalDateTime.now()
        )
        
        val nextId = todoDao.insertTodo(nextTodo)
        val nextTodoWithId = nextTodo.copy(id = nextId)
        notificationHelper.scheduleNotification(nextTodoWithId)
    }
}
