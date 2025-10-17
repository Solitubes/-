package com.example.todoapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.data.TodoItem
import com.example.todoapp.ui.components.TodoItemCard
import com.example.todoapp.ui.viewmodel.TodoViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavController,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showCompleted by viewModel.showCompleted.collectAsState()
    
    val filteredTodos = remember(todos, showCompleted) {
        if (showCompleted) {
            todos
        } else {
            todos.filter { !it.isCompleted }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("待办事项") },
                actions = {
                    IconButton(onClick = { viewModel.toggleShowCompleted() }) {
                        Icon(
                            imageVector = if (showCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                            contentDescription = if (showCompleted) "隐藏已完成" else "显示已完成"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_todo") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加待办事项")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                filteredTodos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (showCompleted) "暂无已完成的待办事项" else "暂无待办事项",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击 + 按钮添加第一个待办事项",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTodos) { todo ->
                            TodoItemCard(
                                todo = todo,
                                onToggleCompletion = { viewModel.toggleTodoCompletion(todo) },
                                onDelete = { viewModel.deleteTodo(todo) },
                                onEdit = { navController.navigate("edit_todo/${todo.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}
