package com.demo.smarttask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SearchTab(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit = {},
    selectedFilter: String,
    onSelectedFilterChange: (String) -> Unit = {}
) {
    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchQueryChange(it) },
            label = { Text(text = "Search Task") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Pending", "Completed").forEach { filter ->

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        onSelectedFilterChange(filter)
                    },
                    label = {
                        Text(filter)
                    }
                )
            }
        }
    }
}