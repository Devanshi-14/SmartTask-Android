
package com.demo.smarttask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demo.smarttask.data.TaskPriority

@Composable
fun TaskPrioritySelector(
    selectedPriority: TaskPriority,
    onPriorityChanged: (TaskPriority) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp
        )
    ) {
        TaskPriority.entries.forEach { priority ->

            FilterChip(
                modifier = Modifier.weight(1f),
                selected = priority == selectedPriority,
                onClick = {
                    onPriorityChanged(priority)
                },
                label = {
                    Text(
                        text = priority.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
