package com.example.kriptogan.data

import android.content.Context
import com.example.kriptogan.data.auth.GmailAuthService
import com.example.kriptogan.data.database.DatabaseProvider
import com.example.kriptogan.data.email.EmailService
import com.example.kriptogan.data.excel.ExcelGeneratorService
import com.example.kriptogan.data.repository.EmailConfigRepository
import com.example.kriptogan.data.repository.SettingsRepository
import com.example.kriptogan.data.repository.WorkDayRepository

object RepositoryProvider {
    private var workDayRepository: WorkDayRepository? = null
    private var settingsRepository: SettingsRepository? = null
    private var emailConfigRepository: EmailConfigRepository? = null
    private var gmailAuthService: GmailAuthService? = null
    private var excelGeneratorService: ExcelGeneratorService? = null
    private var emailService: EmailService? = null

    fun initialize(context: Context) {
        val database = DatabaseProvider.getDatabase(context)
        workDayRepository = WorkDayRepository(
            database.workDayDao(),
            database.timeEntryDao()
        )
        settingsRepository = SettingsRepository(database.appSettingsDao())
        emailConfigRepository = EmailConfigRepository(database.emailConfigDao())
        gmailAuthService = GmailAuthService(context)
        excelGeneratorService = ExcelGeneratorService(context)
        emailService = EmailService(context, gmailAuthService!!)
    }

    fun getWorkDayRepository(): WorkDayRepository {
        return workDayRepository
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }

    fun getSettingsRepository(): SettingsRepository {
        return settingsRepository
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }

    fun getEmailConfigRepository(): EmailConfigRepository {
        return emailConfigRepository
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }

    fun getGmailAuthService(): GmailAuthService {
        return gmailAuthService
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }

    fun getExcelGeneratorService(): ExcelGeneratorService {
        return excelGeneratorService
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }

    fun getEmailService(): EmailService {
        return emailService
            ?: throw IllegalStateException("RepositoryProvider not initialized. Call initialize() first.")
    }
}
