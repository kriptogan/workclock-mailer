package com.example.kriptogan.ui.daydetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.ui.daydetail.components.DayTypeSelector
import com.example.kriptogan.ui.daydetail.components.TimeEntryDialog
import com.example.kriptogan.ui.daydetail.components.TimeEntryList
import com.example.kriptogan.ui.daydetail.components.TimerControls
import com.example.kriptogan.util.DateUtils
import com.example.kriptogan.util.TimeCalculationUtils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<TimeEntry?>(null) }
    
    // Update elapsed time every second when timer is running
    val elapsedTime by produceState(initialValue = 0L) {
        while (uiState.isTimerRunning) {
            value = viewModel.getElapsedTime()
            kotlinx.coroutines.delay(1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(DateUtils.formatDate(uiState.date)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Day Summary
                uiState.calculation?.let { calc: TimeCalculationUtils.DailyCalculation ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Day Summary",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Worked: ${String.format("%.2f", calc.totalWorked)} h")
                                Text("Expected: ${calc.expected} h")
                            }
                            if (calc.overtime > 0) {
                                Text(
                                    text = "Overtime: +${String.format("%.2f", calc.overtime)} h",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            } else if (calc.missing > 0) {
                                Text(
                                    text = "Missing: -${String.format("%.2f", calc.missing)} h",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Timer Controls
                TimerControls(
                    isRunning = uiState.isTimerRunning,
                    elapsedMinutes = elapsedTime,
                    onStart = { viewModel.startTimer() },
                    onStop = { viewModel.stopTimer() },
                    modifier = Modifier.fillMaxWidth()
                )

                // Day Type Selector
                DayTypeSelector(
                    selectedType = uiState.dayType,
                    onTypeSelected = { viewModel.updateDayType(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Entries Section
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Time Entries",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showAddEntryDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add entry"
                                )
                            }
                        }
                        TimeEntryList(
                            timeEntries = uiState.timeEntries,
                            onEdit = { editingEntry = it },
                            onDelete = { viewModel.deleteTimeEntry(it) },
                            modifier = Modifier.heightIn(max = 300.dp)
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Time Entry Dialog
    if (showAddEntryDialog || editingEntry != null) {
        TimeEntryDialog(
            timeEntry = editingEntry,
            onDismiss = {
                showAddEntryDialog = false
                editingEntry = null
            },
            onSave = { startTime, endTime, breakMinutes ->
                if (editingEntry != null) {
                    viewModel.updateTimeEntry(editingEntry!!, startTime, endTime, breakMinutes)
                } else {
                    viewModel.addTimeEntry(startTime, endTime, breakMinutes)
                }
                editingEntry = null
            },
            defaultBreakMinutes = 30
        )
    }
}
