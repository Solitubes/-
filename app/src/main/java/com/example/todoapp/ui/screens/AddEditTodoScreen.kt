package com.example.todoapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.data.RepeatType
import com.example.todoapp.ui.components.DateTimePicker
import com.example.todoapp.ui.viewmodel.AddEditTodoViewModel
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.datetime
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    navController: NavController,
    todoId: Long? = null,
    viewModel: AddEditTodoViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val repeatType by viewModel.repeatType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showDateTimeDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(todoId) {
        if (todoId != null) {
            viewModel.loadTodo(todoId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (todoId == null) "添加待办事项" else "编辑待办事项") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::updateDescription,
                label = { Text("描述") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            OutlinedTextField(
                value = dueDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                onValueChange = { },
                label = { Text("截止日期") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { showDateTimeDialog = true }) {
                        Text("选择")
                    }
                }
            )
            
            Text(
                text = "重复类型",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                RepeatType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (repeatType == type),
                                onClick = { viewModel.updateRepeatType(type) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (repeatType == type),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (type) {
                                RepeatType.ONCE -> "一次性"
                                RepeatType.DAILY -> "每日"
                                RepeatType.WEEKLY -> "每周"
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (todoId == null) {
                        viewModel.saveTodo { navController.popBackStack() }
                    } else {
                        viewModel.updateTodo(todoId) { navController.popBackStack() }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text(if (todoId == null) "保存" else "更新")
                }
            }
        }
    }
    
    if (showDateTimeDialog) {
        MaterialDialog(
            onDismissRequest = { showDateTimeDialog = false }
        ) {
            datetime(
                initialDateTime = dueDate,
                title = "选择日期和时间",
                onDateTimeChange = { newDateTime ->
                    viewModel.updateDueDate(newDateTime)
                    showDateTimeDialog = false
                }
            )
        }
    }
}
