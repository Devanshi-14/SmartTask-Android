package com.demo.smarttask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.demo.smarttask.notification.NotificationHelper

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext

    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
        }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    val database = remember(context) {
        DatabaseProvider.getDatabase(context)
    }
    val taskRemoteDataSource = remember {
        TodoRemoteDataSource(RetrofitClient.todoApiService)
    }
    val repository = remember(database) {
        TaskRepository(taskDao = database.taskDao(), taskRemoteDataSource = taskRemoteDataSource)
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