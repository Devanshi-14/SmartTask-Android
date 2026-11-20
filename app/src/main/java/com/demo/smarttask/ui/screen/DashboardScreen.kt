package com.demo.smarttask.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.smarttask.data.Task
import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.data.local.TaskDao
import com.demo.smarttask.data.local.TaskEntity
import com.demo.smarttask.repository.TaskRepository
import com.demo.smarttask.ui.components.SearchTab
import com.demo.smarttask.ui.components.SmartTaskHeader
import com.demo.smarttask.ui.components.TaskItem
import com.demo.smarttask.ui.components.TaskSummaryCard
import com.demo.smarttask.ui.theme.SmartTaskTheme
import com.demo.smarttask.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun DashBoardScreen(
    onAddEditTaskClick: (Task?) -> Unit,
    onTaskDetailsClick: (Task?) -> Unit,
    taskViewModel: TaskViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()
    val apiLoading by taskViewModel.apiLoading.collectAsState()
    val apiError by taskViewModel.apiError.collectAsState()
    val syncLoading by taskViewModel.syncLoading.collectAsState()
    val syncMessage by taskViewModel.syncMessage.collectAsState()
    val syncError by taskViewModel.syncError.collectAsState()

    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedDeleteTask by remember {
        mutableStateOf<Task?>(null)
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedFilter by rememberSaveable {
        mutableStateOf("Pending")
    }


    // Summary calculations
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val pendingTasks = tasks.count { !it.isCompleted }

    // Search and Filter
    val filteredTasks = remember(tasks, searchQuery, selectedFilter) {
        tasks.filter { task ->

            val matchSearch = task.title.contains(
                searchQuery,
                ignoreCase = true
            ) || task.description.contains(
                searchQuery,
                ignoreCase = true
            )

            val matchFilter = when (selectedFilter) {
                "Pending" -> !task.isCompleted
                "Completed" -> task.isCompleted
                else -> true
            }

            matchSearch && matchFilter
        }
    }

    // Priority sorting: HIGH -> MEDIUM -> LOW
    val sortedTaskList = filteredTasks.sortedBy { task ->
        when (task.priority) {
            TaskPriority.HIGH -> 1
            TaskPriority.MEDIUM -> 2
            TaskPriority.LOW -> 3
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header
        SmartTaskHeader(
            onSyncClicked = {
                taskViewModel.fetchTodos()
                taskViewModel.syncPendingTasks()
            }
        )
        if(apiLoading){
            CircularProgressIndicator()
        }
        apiError?.let {
            error->
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = {
                taskViewModel.fetchTodos()
            }) {
                Text(
                    text = "retry"
                )
            }
        }
        if (syncLoading) {
            CircularProgressIndicator()
        }

        syncMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        syncError?.let { error ->
            Text(
                text = "Sync Error: $error",
                color = MaterialTheme.colorScheme.error
            )
        }
        // Greeting
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Organize your day, one task at a time.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskSummaryCard(
                title = "Total Tasks",
                count = totalTasks
            )

            TaskSummaryCard(
                title = "Pending",
                count = pendingTasks
            )

            TaskSummaryCard(
                title = "Completed",
                count = completedTasks
            )
        }

        // Add Task Button
        Button(
            onClick = {
                onAddEditTaskClick(null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            Text(
                text = "Add New Task",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Search and Filters
        SearchTab(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
            },
            selectedFilter = selectedFilter,
            onSelectedFilterChange = {
                selectedFilter = it
            }
        )

        // Task List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${filteredTasks.size} Tasks",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Task List / Empty State
        if (sortedTaskList.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) {
                        "No matching tasks found"
                    } else {
                        "No tasks found"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = sortedTaskList,
                    key = { it.id }
                ) { task ->

                    TaskItem(
                        task = task,
                        onCompletionChange = { checked ->
                            taskViewModel.updateTaskCompletion(
                                task.id,
                                checked
                            )
                        },
                        onDelete = {
                            selectedDeleteTask = task
                            showDeleteDialog = true
                        },
                        onEdit = {
                            onAddEditTaskClick(task)
                        },
                        onDetailsClick = {
                            onTaskDetailsClick(task)
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedDeleteTask != null) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedDeleteTask = null
            },
            title = {
                Text("Delete Task?")
            },
            text = {
                Text("Are you sure you want to delete this task?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDeleteTask?.let { task ->
                            taskViewModel.deleteTask(task.id)
                        }

                        showDeleteDialog = false
                        selectedDeleteTask = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedDeleteTask = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
