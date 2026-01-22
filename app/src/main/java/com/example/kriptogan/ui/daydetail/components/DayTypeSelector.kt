package com.example.kriptogan.ui.daydetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.model.DayType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTypeSelector(
    selectedType: DayType,
    onTypeSelected: (DayType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dayTypes = DayType.values()

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Day Type",
                style = MaterialTheme.typography.titleMedium
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getDayTypeLabel(selectedType),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Select day type") },
                    supportingText = {
                        Text(
                            text = getDayTypeDescription(selectedType),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    dayTypes.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = getDayTypeLabel(type),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = getDayTypeDescription(type),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onTypeSelected(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getDayTypeLabel(type: DayType): String {
    return when (type) {
        DayType.NORMAL -> "Normal Day"
        DayType.HOLIDAY -> "Holiday"
        DayType.HOLIDAY_EVENING -> "Holiday Evening"
        DayType.DAY_OFF -> "Day Off"
        DayType.SEMI_DAY_OFF -> "Semi-Day Off"
    }
}

private fun getDayTypeDescription(type: DayType): String {
    return when (type) {
        DayType.NORMAL -> "Regular work day"
        DayType.HOLIDAY -> "No work expected"
        DayType.HOLIDAY_EVENING -> "Reduced hours expected"
        DayType.DAY_OFF -> "Personal day off"
        DayType.SEMI_DAY_OFF -> "Partial hours expected"
    }
}
