package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TodoItem
import com.example.todoapp.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _showCompleted = MutableStateFlow(false)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()
    
    init {
        loadTodos()
    }
    
    fun loadTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTodos().collect { todoList ->
                    _todos.value = todoList
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }
    
    fun toggleTodoCompletion(todo: TodoItem) {
        viewModelScope.launch {
            repository.toggleTodoCompletion(todo)
        }
    }
    
    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
    
    fun getFilteredTodos(): List<TodoItem> {
        return if (_showCompleted.value) {
            _todos.value
        } else {
            _todos.value.filter { !it.isCompleted }
        }
    }
}
