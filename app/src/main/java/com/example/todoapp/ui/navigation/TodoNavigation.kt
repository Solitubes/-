package com.example.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.screens.AddEditTodoScreen
import com.example.todoapp.ui.screens.TodoListScreen

@Composable
fun TodoNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "todo_list"
    ) {
        composable("todo_list") {
            TodoListScreen(navController = navController)
        }
        
        composable("add_todo") {
            AddEditTodoScreen(navController = navController)
        }
        
        composable("edit_todo/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toLongOrNull()
            AddEditTodoScreen(
                navController = navController,
                todoId = todoId
            )
        }
    }
}
