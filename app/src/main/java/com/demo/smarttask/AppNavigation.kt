package com.demo.smarttask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.smarttask.data.Task
import com.demo.smarttask.data.local.DatabaseProvider
import com.demo.smarttask.data.remote.RetrofitClient
import com.demo.smarttask.data.remote.TodoRemoteDataSource
import com.demo.smarttask.repository.TaskRepository
import com.demo.smarttask.ui.screen.AddEditTaskScreen
import com.demo.smarttask.ui.screen.DashBoardScreen
import com.demo.smarttask.ui.screen.TaskDetailsScreen
import com.demo.smarttask.viewmodel.TaskViewModel
import com.demo.smarttask.viewmodel.TaskViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext

    val database = remember(context) {
        DatabaseProvider.getDatabase(context)
    }
    val taskRemoteDataSource = remember {
        TodoRemoteDataSource(RetrofitClient.todoApiService)
    }
    val repository = remember(database) {
        TaskRepository(taskDao = database.taskDao(), taskRemoteDataSource = taskRemoteDataSource )
    }

    val factory = remember(repository) {
        TaskViewModelFactory(repository)
    }

    val taskViewModel: TaskViewModel = viewModel(
        factory = factory
    )

    var selectedTask by remember {
        mutableStateOf<Task?>(null)
    }
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            DashBoardScreen(
                onAddEditTaskClick = { task ->
                    selectedTask = task
                    navController.navigate("add_edit_task")
                },
                taskViewModel = taskViewModel,
                onTaskDetailsClick = { task ->
                    selectedTask = task
                    navController.navigate("task_details")
                }
            )
        }
        composable("add_edit_task") {
            AddEditTaskScreen(
                task = selectedTask,
                onBackClick = { navController.popBackStack() },
                taskViewModel = taskViewModel
            )
        }
        composable("task_details") {
            selectedTask?.let { task ->
                TaskDetailsScreen(
                    task = task,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }

    }

}