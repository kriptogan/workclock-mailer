package com.example.kriptogan.ui.emailconfig

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptogan.data.auth.GmailAuthService
import com.example.kriptogan.data.model.EmailConfig
import com.example.kriptogan.data.repository.EmailConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmailConfigUiState(
    val senderEmail: String? = null,
    val recipients: List<String> = emptyList(),
    val subject: String? = null,
    val body: String? = null,
    val autoSendEnabled: Boolean = false,
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveMessage: String? = null
)

class EmailConfigViewModel(
    application: Application,
    private val emailConfigRepository: EmailConfigRepository,
    private val gmailAuthService: GmailAuthService
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EmailConfigUiState())
    val uiState: StateFlow<EmailConfigUiState> = _uiState.asStateFlow()

    init {
        loadConfig()
        checkAuthStatus()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val config = emailConfigRepository.getEmailConfig()
                if (config != null) {
                    _uiState.value = _uiState.value.copy(
                        senderEmail = config.senderEmail,
                        recipients = config.recipients,
                        subject = config.subject,
                        body = config.body,
                        autoSendEnabled = config.autoSendEnabled,
                        isLoading = false
                    )
                } else {
                    emailConfigRepository.initializeDefaultConfig()
                    loadConfig()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun checkAuthStatus() {
        val isSignedIn = gmailAuthService.isSignedIn()
        val email = if (isSignedIn) gmailAuthService.getSignedInEmail() else null
        _uiState.value = _uiState.value.copy(
            isSignedIn = isSignedIn,
            senderEmail = email ?: _uiState.value.senderEmail
        )
    }

    fun handleSignInResult(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount?) {
        viewModelScope.launch {
            if (account != null) {
                // Get tokens from account (this would need to be implemented with OAuth flow)
                val email = account.email
                _uiState.value = _uiState.value.copy(
                    isSignedIn = true,
                    senderEmail = email
                )
                // Save to repository
                updateSenderEmail(email)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            gmailAuthService.signOut()
            _uiState.value = _uiState.value.copy(
                isSignedIn = false,
                senderEmail = null
            )
            updateSenderEmail(null)
        }
    }

    fun addRecipient(email: String) {
        val currentRecipients = _uiState.value.recipients.toMutableList()
        if (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!currentRecipients.contains(email)) {
                currentRecipients.add(email)
                _uiState.value = _uiState.value.copy(recipients = currentRecipients)
            }
        }
    }

    fun removeRecipient(email: String) {
        val currentRecipients = _uiState.value.recipients.toMutableList()
        currentRecipients.remove(email)
        _uiState.value = _uiState.value.copy(recipients = currentRecipients)
    }

    fun updateSubject(subject: String) {
        _uiState.value = _uiState.value.copy(subject = subject.ifBlank { null })
    }

    fun updateBody(body: String) {
        _uiState.value = _uiState.value.copy(body = body.ifBlank { null })
    }

    fun toggleAutoSend(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoSendEnabled = enabled)
        viewModelScope.launch {
            emailConfigRepository.updateAutoSend(enabled, getApplication<android.app.Application>())
        }
    }

    private fun updateSenderEmail(email: String?) {
        viewModelScope.launch {
            val current = emailConfigRepository.getEmailConfig()
            if (current != null) {
                emailConfigRepository.updateEmailConfig(current.copy(senderEmail = email))
            }
        }
    }

    fun saveConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveMessage = null)
            try {
                val currentState = _uiState.value
                val config = EmailConfig(
                    id = 1,
                    senderEmail = currentState.senderEmail,
                    recipients = currentState.recipients,
                    subject = currentState.subject,
                    body = currentState.body,
                    autoSendEnabled = currentState.autoSendEnabled,
                    accessToken = gmailAuthService.getAccessToken(),
                    refreshToken = gmailAuthService.getRefreshToken()
                )
                emailConfigRepository.updateEmailConfig(config)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Email configuration saved successfully"
                )
                kotlinx.coroutines.delay(3000)
                _uiState.value = _uiState.value.copy(saveMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Error saving configuration: ${e.message}"
                )
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveMessage = null)
    }
}
