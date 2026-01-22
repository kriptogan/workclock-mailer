package com.example.kriptogan.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.ui.settings.components.NumberInputField
import com.example.kriptogan.ui.settings.components.WorkDaysSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for save message
    uiState.saveMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearSaveMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveSettings() },
                modifier = Modifier.padding(16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save"
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Work Days Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Work Days",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Select the days of the week you work",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        WorkDaysSelector(
                            selectedDays = uiState.workDays,
                            onDayToggled = { viewModel.toggleWorkDay(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Work Hours Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Work Hours Per Day",
                            style = MaterialTheme.typography.titleMedium
                        )
                        NumberInputField(
                            value = uiState.workHoursPerDay,
                            onValueChange = { viewModel.updateWorkHoursPerDay(it) },
                            label = "Hours",
                            helperText = "Expected work hours per work day",
                            modifier = Modifier.fillMaxWidth(),
                            minValue = 1,
                            maxValue = 24
                        )
                    }
                }

                // Break Time Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Break Time Deduction",
                            style = MaterialTheme.typography.titleMedium
                        )
                        NumberInputField(
                            value = uiState.breakMinutes,
                            onValueChange = { viewModel.updateBreakMinutes(it) },
                            label = "Minutes",
                            helperText = "Default break time to deduct from work sessions",
                            modifier = Modifier.fillMaxWidth(),
                            minValue = 0,
                            maxValue = 480
                        )
                    }
                }

                // Holiday Evening Hours Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Holiday Evening Hours",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Hours expected to work on holiday evenings",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        NumberInputField(
                            value = uiState.holidayEveningHours,
                            onValueChange = { viewModel.updateHolidayEveningHours(it) },
                            label = "Hours",
                            helperText = "Expected work hours on holiday evenings",
                            modifier = Modifier.fillMaxWidth(),
                            minValue = 0,
                            maxValue = 24
                        )
                    }
                }

                // Semi-Day Off Hours Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Semi-Day Off Hours",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Hours expected to work on semi-day off",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        NumberInputField(
                            value = uiState.semiDayOffHours,
                            onValueChange = { viewModel.updateSemiDayOffHours(it) },
                            label = "Hours",
                            helperText = "Expected work hours on semi-day off",
                            modifier = Modifier.fillMaxWidth(),
                            minValue = 0,
                            maxValue = 24
                        )
                    }
                }

                // Spacer for FAB
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
