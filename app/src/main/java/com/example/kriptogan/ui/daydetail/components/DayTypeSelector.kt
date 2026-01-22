package com.example.kriptogan.ui.daydetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.model.DayType

@Composable
fun DayTypeSelector(
    selectedType: DayType,
    onTypeSelected: (DayType) -> Unit,
    modifier: Modifier = Modifier
) {
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
            
            DayType.values().forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTypeSelected(type) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
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
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { onTypeSelected(type) }
                    )
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
