package com.example.kriptogan.data.repository

import com.example.kriptogan.data.database.dao.EmailConfigDao
import com.example.kriptogan.data.model.EmailConfig
import kotlinx.coroutines.flow.Flow

class EmailConfigRepository(
    private val emailConfigDao: EmailConfigDao
) {
    suspend fun getEmailConfig(): EmailConfig? {
        return emailConfigDao.getEmailConfig()
    }

    fun getEmailConfigFlow(): Flow<EmailConfig?> {
        return emailConfigDao.getEmailConfigFlow()
    }

    suspend fun updateEmailConfig(config: EmailConfig) {
        emailConfigDao.updateEmailConfig(config)
    }

    suspend fun updateAutoSend(enabled: Boolean, context: android.content.Context? = null) {
        val current = emailConfigDao.getEmailConfig()
        if (current != null) {
            emailConfigDao.updateEmailConfig(current.copy(autoSendEnabled = enabled))
            
            // Schedule or cancel auto-send work
            context?.let {
                if (enabled) {
                    com.example.kriptogan.data.work.WorkManagerHelper.scheduleAutoSend(it)
                } else {
                    com.example.kriptogan.data.work.WorkManagerHelper.cancelAutoSend(it)
                }
            }
        }
    }

    suspend fun updateRecipients(recipients: List<String>) {
        val current = emailConfigDao.getEmailConfig()
        if (current != null) {
            emailConfigDao.updateEmailConfig(current.copy(recipients = recipients))
        }
    }

    suspend fun updateEmailTemplate(subject: String?, body: String?) {
        val current = emailConfigDao.getEmailConfig()
        if (current != null) {
            emailConfigDao.updateEmailConfig(current.copy(subject = subject, body = body))
        }
    }

    suspend fun updateTokens(accessToken: String?, refreshToken: String?) {
        val current = emailConfigDao.getEmailConfig()
        if (current != null) {
            emailConfigDao.updateEmailConfig(
                current.copy(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        }
    }

    suspend fun initializeDefaultConfig() {
        val existing = emailConfigDao.getEmailConfig()
        if (existing == null) {
            val defaultConfig = EmailConfig(
                id = 1,
                senderEmail = null,
                recipients = emptyList(),
                subject = null,
                body = null,
                autoSendEnabled = false,
                accessToken = null,
                refreshToken = null
            )
            emailConfigDao.insertEmailConfig(defaultConfig)
        }
    }
}
