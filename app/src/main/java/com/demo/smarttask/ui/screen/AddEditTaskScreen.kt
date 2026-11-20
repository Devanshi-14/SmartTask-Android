package com.demo.smarttask.ui.screen

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.smarttask.data.Task
import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.data.local.TaskDao
import com.demo.smarttask.data.local.TaskEntity
import com.demo.smarttask.repository.TaskRepository
import com.demo.smarttask.ui.components.TaskForm
import com.demo.smarttask.ui.theme.SmartTaskTheme
import com.demo.smarttask.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun AddEditTaskScreen(task: Task?, onBackClick: () -> Unit, taskViewModel: TaskViewModel = viewModel()) {

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember {
        mutableStateOf( task?.description?:"")
    }
    var selectedTask by remember {
        mutableStateOf(task)
    }

    var selectedPriority by remember {
        mutableStateOf(task?.priority ?: TaskPriority.MEDIUM)
    }

    var selectedDueDate: LocalDate? by rememberSaveable(selectedTask?.id) {
        mutableStateOf(selectedTask?.dueDate)
    }

    var showDatePicker by rememberSaveable() {
        mutableStateOf(false)
    }
    TaskForm(
        task = selectedTask,
        title = title,
        description = description,
        selectedPriority = selectedPriority,
        selectedDueDate = selectedDueDate?.let {
            LocalDate.parse(it.toString())
        },
        onTitleChange = { title = it },
        onDescriptionChange = { description = it },
        onPriorityChange = { selectedPriority = it },
        onDatePickerClick = {
            showDatePicker = true
        },
        onSave = {
            if (selectedTask == null) {
                taskViewModel.addTask(
                    title, description, selectedPriority, selectedDueDate
                )
            } else {
                selectedTask?.let {
                    taskViewModel.updateTask(
                        it.id, it.copy(
                            title = title,
                            description = description,
                            priority = selectedPriority,
                            dueDate = selectedDueDate
                        )
                    )
                }
                selectedTask = null
            }
            onBackClick()
        },
        onCancel = {
            onBackClick()
        },
    )
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(onDismissRequest = {
            showDatePicker = false
        }, confirmButton = {
            TextButton(
                onClick = {
                    selectedDueDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }

                    showDatePicker = false
                }) {
                Text("Ok")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showDatePicker = false
                }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    }
}
