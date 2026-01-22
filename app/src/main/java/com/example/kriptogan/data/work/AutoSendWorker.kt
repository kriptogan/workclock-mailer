package com.example.kriptogan.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kriptogan.data.RepositoryProvider
import com.example.kriptogan.data.email.EmailService
import com.example.kriptogan.data.excel.ExcelGeneratorService
import com.example.kriptogan.data.repository.EmailConfigRepository
import com.example.kriptogan.data.repository.WorkDayRepository
import java.time.LocalDate
import java.time.YearMonth

class AutoSendWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val emailConfigRepository = RepositoryProvider.getEmailConfigRepository()
            val emailConfig = emailConfigRepository.getEmailConfig()
            
            // Check if auto-send is enabled
            if (emailConfig?.autoSendEnabled != true) {
                return Result.success() // Not enabled, skip
            }
            
            // Check if it's first day of month
            val today = LocalDate.now()
            if (today.dayOfMonth != 1) {
                return Result.success() // Not first day, skip
            }
            
            // Get previous month
            val previousMonth = today.minusMonths(1)
            val yearMonth = YearMonth.of(previousMonth.year, previousMonth.monthValue)
            
            // Get work days for previous month
            val workDayRepository = RepositoryProvider.getWorkDayRepository()
            val workDays = workDayRepository.getWorkDaysForMonth(
                yearMonth.year,
                yearMonth.monthValue
            )
            
            // Generate Excel
            val excelGeneratorService = RepositoryProvider.getExcelGeneratorService()
            val excelFile = excelGeneratorService.generateExcelFile(yearMonth, workDays)
            
            // Send email
            val emailService = RepositoryProvider.getEmailService()
            val subject = emailConfig.subject ?: "Work Hours Report - ${yearMonth.month} ${yearMonth.year}"
            val body = emailConfig.body ?: "Please find attached the work hours report for ${yearMonth.month} ${yearMonth.year}."
            
            emailService.sendEmail(
                recipients = emailConfig.recipients,
                subject = subject,
                body = body,
                excelFile = excelFile
            )
            
            // Clean up file after sending (optional)
            excelFile.delete()
            
            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            android.util.Log.e("AutoSendWorker", "Failed to send auto email", e)
            Result.retry()
        }
    }
}
