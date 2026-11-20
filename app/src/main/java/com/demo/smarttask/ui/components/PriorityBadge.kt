package com.demo.smarttask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demo.smarttask.data.TaskPriority
import com.demo.smarttask.ui.theme.SmartTaskTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PriorityBadge(taskPriority: TaskPriority, dueDate: LocalDate?) {

    val priorityColor = when (taskPriority) {
        TaskPriority.HIGH -> Color.Red
        TaskPriority.MEDIUM -> Color(0xFFFF9800)
        TaskPriority.LOW -> Color(0xFF4CAF50)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = priorityColor.copy(alpha = 0.12f)
        ) {
            Text(
                text = taskPriority.name,
                color = priorityColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
            )
        }

        if (dueDate != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Due Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = dueDate.format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy")
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPriorityBadge(){
    SmartTaskTheme {
        Column  {
            PriorityBadge(TaskPriority.HIGH, LocalDate.now())
            PriorityBadge(TaskPriority.LOW, LocalDate.now())
            PriorityBadge(TaskPriority.MEDIUM, LocalDate.now())
        }
    }
}