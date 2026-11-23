package com.demo.smarttask.viewmodel

import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskViewModelTest {

    private lateinit var repository: TaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        repository = mockk(relaxed = true)

        every { repository.tasks } returns flowOf(emptyList())

        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Test 1: Task List Load
    @Test
    fun `task list should load successfully`() {
        assertEquals(emptyList<Any>(), viewModel.tasks.value)
    }

    // Test 2: Add Task
    @Test
    fun `add task should call repository`() = runTest {
        viewModel.addTask(
            title = "Learn Hilt",
            description = "Practice dependency injection",
            priority = TaskPriority.MEDIUM,
            dueDate = null
        )

        coVerify {
            repository.addTask(
                "Learn Hilt",
                "Practice dependency injection",
                TaskPriority.MEDIUM,
                null
            )
        }
    }

    // Test 3: Update Task Completion
    @Test
    fun `update task completion should call repository`() = runTest {
        viewModel.updateTaskCompletion(1, true)

        coVerify {
            repository.updateTaskCompletion(1, true)
        }
    }

    // Test 4: Delete Task
    @Test
    fun `delete task should call repository`() = runTest {
        viewModel.deleteTask(1)

        coVerify {
            repository.deleteTask(1)
        }
    }

    // Test 5: API Error Handling
    @Test
    fun `fetch todos should update error state when api fails`() = runTest {
        coEvery {
            repository.fetchRemoteTodos()
        } throws RuntimeException("Network error")

        viewModel.fetchTodos()

        assertEquals("Network error", viewModel.apiError.value)
        assertEquals(false, viewModel.apiLoading.value)
    }
}