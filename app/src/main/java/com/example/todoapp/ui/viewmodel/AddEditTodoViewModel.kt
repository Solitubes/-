package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.RepeatType
import com.example.todoapp.data.TodoItem
import com.example.todoapp.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _dueDate = MutableStateFlow(LocalDateTime.now().plusDays(1))
    val dueDate: StateFlow<LocalDateTime> = _dueDate.asStateFlow()
    
    private val _repeatType = MutableStateFlow(RepeatType.ONCE)
    val repeatType: StateFlow<RepeatType> = _repeatType.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun updateTitle(title: String) {
        _title.value = title
    }
    
    fun updateDescription(description: String) {
        _description.value = description
    }
    
    fun updateDueDate(date: LocalDateTime) {
        _dueDate.value = date
    }
    
    fun updateRepeatType(repeatType: RepeatType) {
        _repeatType.value = repeatType
    }
    
    fun loadTodo(todoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTodoById(todoId)?.let { todo ->
                    _title.value = todo.title
                    _description.value = todo.description
                    _dueDate.value = todo.dueDate
                    _repeatType.value = todo.repeatType
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveTodo(onSuccess: () -> Unit) {
        if (_title.value.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val todo = TodoItem(
                    title = _title.value.trim(),
                    description = _description.value.trim(),
                    dueDate = _dueDate.value,
                    repeatType = _repeatType.value
                )
                repository.insertTodo(todo)
                onSuccess()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateTodo(todoId: Long, onSuccess: () -> Unit) {
        if (_title.value.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val todo = TodoItem(
                    id = todoId,
                    title = _title.value.trim(),
                    description = _description.value.trim(),
                    dueDate = _dueDate.value,
                    repeatType = _repeatType.value,
                    lastModified = LocalDateTime.now()
                )
                repository.updateTodo(todo)
                onSuccess()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun reset() {
        _title.value = ""
        _description.value = ""
        _dueDate.value = LocalDateTime.now().plusDays(1)
        _repeatType.value = RepeatType.ONCE
    }
}
