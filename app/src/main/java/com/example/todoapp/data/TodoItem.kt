package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: LocalDateTime,
    val isCompleted: Boolean = false,
    val repeatType: RepeatType = RepeatType.ONCE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastModified: LocalDateTime = LocalDateTime.now()
)

enum class RepeatType {
    ONCE,       // 一次性
    DAILY,      // 每日
    WEEKLY      // 每周
}
