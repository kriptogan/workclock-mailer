package com.example.kriptogan.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.repository.WorkDayRepository
import com.example.kriptogan.ui.calendar.components.DayCard
import com.example.kriptogan.ui.calendar.components.MonthlySummary
import com.example.kriptogan.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    settings: com.example.kriptogan.data.model.AppSettings?,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendarDays = DateUtils.getCalendarDays(uiState.currentMonth)
    val dayOfWeekHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column(modifier = modifier.fillMaxSize()) {
        // Month Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateToPreviousMonth() },
                enabled = true
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            
            Text(
                text = DateUtils.formatMonthYear(uiState.currentMonth),
                style = MaterialTheme.typography.titleLarge
            )
            
            IconButton(
                onClick = { viewModel.navigateToNextMonth() },
                enabled = uiState.currentMonth < java.time.YearMonth.now()
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        // Monthly Summary
        MonthlySummary(
            calculation = uiState.monthlyCalculation,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        Column(modifier = Modifier.fillMaxWidth()) {
            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayOfWeekHeaders.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Calendar days grid - show all days for alignment, but only display current month days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(calendarDays) { date ->
                    val isCurrentMonth = date.year == uiState.currentMonth.year && 
                                       date.month == uiState.currentMonth.month
                    
                    if (isCurrentMonth) {
                        val workDayWithEntries = uiState.workDaysWithEntries.find { 
                            it.workDay.date == date 
                        }
                        DayCard(
                            date = date,
                            workDayWithEntries = workDayWithEntries,
                            settings = settings,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Empty space for days outside current month (maintains grid alignment)
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
