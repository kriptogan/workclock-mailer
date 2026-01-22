package com.example.kriptogan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "email_config")
data class EmailConfig(
    @PrimaryKey
    val id: Long = 1, // Always 1 for singleton
    val senderEmail: String? = null,
    val recipients: List<String> = emptyList(),
    val subject: String? = null,
    val body: String? = null,
    val autoSendEnabled: Boolean = false,
    val accessToken: String? = null, // OAuth2 access token (should be encrypted)
    val refreshToken: String? = null  // OAuth2 refresh token (should be encrypted)
)
