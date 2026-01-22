package com.example.kriptogan.ui.calendar.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.model.DayType
import com.example.kriptogan.data.repository.WorkDayWithEntries
import com.example.kriptogan.util.DateUtils
import com.example.kriptogan.util.TimeCalculationUtils
import java.time.LocalDate

@Composable
fun DayCard(
    date: LocalDate,
    workDayWithEntries: WorkDayWithEntries?,
    settings: com.example.kriptogan.data.model.AppSettings?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = DateUtils.isToday(date)
    val isFuture = DateUtils.isFuture(date)
    // Weekend is any day that is NOT in the work days set from settings
    val isWeekend = settings?.let { date.dayOfWeek !in it.workDays } ?: false
    
    val dayType = workDayWithEntries?.workDay?.dayType ?: DayType.NORMAL
    val calculation = workDayWithEntries?.let { 
        settings?.let { s -> TimeCalculationUtils.calculateDaily(it, s) }
    }
    
    val backgroundColor = when {
        dayType == DayType.HOLIDAY -> Color(0xFF4CAF50) // Green
        dayType == DayType.HOLIDAY_EVENING -> Color(0xFF81C784) // Light green
        dayType == DayType.DAY_OFF -> Color(0xFF2196F3) // Blue
        dayType == DayType.SEMI_DAY_OFF -> Color(0xFF64B5F6) // Light blue
        isWeekend -> Color(0xFF9E9E9E) // Grey
        else -> Color.White // White for normal days
    }
    
    val borderColor = if (isToday) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = if (isToday) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = !isFuture) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            
            if (calculation != null && calculation.totalWorked > 0) {
                Text(
                    text = String.format("%.1fh", calculation.totalWorked),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (calculation.overtime > 0) {
                    Text(
                        text = "+${String.format("%.1fh", calculation.overtime)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else if (calculation.missing > 0) {
                    Text(
                        text = "-${String.format("%.1fh", calculation.missing)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Day type indicator
            if (dayType != DayType.NORMAL) {
                Text(
                    text = when (dayType) {
                        DayType.HOLIDAY -> "H"
                        DayType.HOLIDAY_EVENING -> "HE"
                        DayType.DAY_OFF -> "DO"
                        DayType.SEMI_DAY_OFF -> "SDO"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
