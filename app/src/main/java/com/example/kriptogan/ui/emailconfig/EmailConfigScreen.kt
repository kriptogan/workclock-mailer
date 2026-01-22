package com.example.kriptogan.ui.emailconfig

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kriptogan.ui.emailconfig.components.AddRecipientDialog
import com.example.kriptogan.ui.emailconfig.components.GmailAuthSection
import com.example.kriptogan.ui.emailconfig.components.RecipientsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailConfigScreen(
    viewModel: EmailConfigViewModel,
    onNavigateBack: () -> Unit = {},
    onSignInRequest: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddRecipientDialog by remember { mutableStateOf(false) }
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
                title = { Text("Email Configuration") },
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
                onClick = { viewModel.saveConfig() },
                modifier = Modifier.padding(bottom = 80.dp, end = 16.dp)
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
                // Gmail Auth Section
                GmailAuthSection(
                    isSignedIn = uiState.isSignedIn,
                    email = uiState.senderEmail,
                    onSignInClick = onSignInRequest,
                    onSignOutClick = { viewModel.signOut() },
                    modifier = Modifier.fillMaxWidth()
                )

                // Recipients Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                text = "Recipients",
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { showAddRecipientDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add recipient"
                                )
                            }
                        }
                        Text(
                            text = "Email addresses that will receive the work hours report",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        RecipientsList(
                            recipients = uiState.recipients,
                            onRemoveRecipient = { viewModel.removeRecipient(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        )
                    }
                }

                // Email Template Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Email Template",
                            style = MaterialTheme.typography.titleMedium
                        )
                        OutlinedTextField(
                            value = uiState.subject ?: "",
                            onValueChange = { viewModel.updateSubject(it) },
                            label = { Text("Subject") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Work Hours Report - [Month] [Year]") }
                        )
                        OutlinedTextField(
                            value = uiState.body ?: "",
                            onValueChange = { viewModel.updateBody(it) },
                            label = { Text("Body") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            maxLines = 5,
                            placeholder = { Text("Please find attached the work hours report...") }
                        )
                    }
                }

                // Auto-Send Section
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
                                text = "Auto-Send",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Automatically send report at end of month",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.autoSendEnabled,
                            onCheckedChange = { viewModel.toggleAutoSend(it) }
                        )
                    }
                }

                // Spacer for FAB and bottom navigation bar
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

    // Add Recipient Dialog
    if (showAddRecipientDialog) {
        AddRecipientDialog(
            onDismiss = { showAddRecipientDialog = false },
            onAdd = { email ->
                viewModel.addRecipient(email)
            }
        )
    }
}
