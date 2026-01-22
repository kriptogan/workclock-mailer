package com.example.kriptogan.ui.daydetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.util.TimeCalculationUtils
import java.time.format.DateTimeFormatter

@Composable
fun TimeEntryList(
    timeEntries: List<TimeEntry>,
    onEdit: (TimeEntry) -> Unit,
    onDelete: (TimeEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    if (timeEntries.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No time entries for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timeEntries) { entry ->
                TimeEntryCard(
                    timeEntry = entry,
                    onEdit = { onEdit(entry) },
                    onDelete = { onDelete(entry) }
                )
            }
        }
    }
}

@Composable
private fun TimeEntryCard(
    timeEntry: TimeEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = timeEntry.startTime.format(formatter)
    val endTime = timeEntry.endTime.format(formatter)
    val netHours = TimeCalculationUtils.calculateNetHours(timeEntry)
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.titleMedium
                )
                if (timeEntry.breakMinutes > 0) {
                    Text(
                        text = "Break: ${timeEntry.breakMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Net: ${String.format("%.2f", netHours)} h",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
