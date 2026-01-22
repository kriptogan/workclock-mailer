package com.example.kriptogan.ui.daydetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.kriptogan.data.model.TimeEntry
import java.time.LocalTime

@Composable
fun TimeEntryDialog(
    timeEntry: TimeEntry?,
    onDismiss: () -> Unit,
    onSave: (LocalTime, LocalTime, Int) -> Unit,
    defaultBreakMinutes: Int = 30,
    modifier: Modifier = Modifier
) {
    var startHour by remember { mutableStateOf(timeEntry?.startTime?.hour ?: LocalTime.now().hour) }
    var startMinute by remember { mutableStateOf(timeEntry?.startTime?.minute ?: 0) }
    var endHour by remember { mutableStateOf(timeEntry?.endTime?.hour ?: LocalTime.now().hour) }
    var endMinute by remember { mutableStateOf(timeEntry?.endTime?.minute ?: 0) }
    var breakMinutes by remember { mutableStateOf(timeEntry?.breakMinutes ?: defaultBreakMinutes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (timeEntry == null) "Add Time Entry" else "Edit Time Entry",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimePickerField(
                        label = "Start Time",
                        hour = startHour,
                        minute = startMinute,
                        onHourChange = { startHour = it },
                        onMinuteChange = { startMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        label = "End Time",
                        hour = endHour,
                        minute = endMinute,
                        onHourChange = { endHour = it },
                        onMinuteChange = { endMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = breakMinutes.toString(),
                    onValueChange = { breakMinutes = it.toIntOrNull() ?: 0 },
                    label = { Text("Break (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val startTime = LocalTime.of(startHour, startMinute)
                            val endTime = LocalTime.of(endHour, endMinute)
                            onSave(startTime, endTime, breakMinutes)
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimePickerField(
    label: String,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = String.format("%02d", hour.coerceIn(0, 23)),
                onValueChange = { onHourChange(it.toIntOrNull()?.coerceIn(0, 23) ?: 0) },
                modifier = Modifier.width(50.dp),
                singleLine = true
            )
            Text(":")
            OutlinedTextField(
                value = String.format("%02d", minute.coerceIn(0, 59)),
                onValueChange = { onMinuteChange(it.toIntOrNull()?.coerceIn(0, 59) ?: 0) },
                modifier = Modifier.width(50.dp),
                singleLine = true
            )
        }
    }
}
