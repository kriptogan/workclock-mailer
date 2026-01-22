package com.example.kriptogan.data.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val AUTO_SEND_WORK_NAME = "auto_send_work"

    fun scheduleAutoSend(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Calculate initial delay to first day of next month at 2 AM
        val initialDelay = calculateInitialDelay()

        val workRequest = PeriodicWorkRequestBuilder<AutoSendWorker>(
            30, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                AUTO_SEND_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    fun cancelAutoSend(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(AUTO_SEND_WORK_NAME)
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val firstOfNextMonth = now
            .plusMonths(1)
            .withDayOfMonth(1)
            .withHour(2)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val delay = Duration.between(now, firstOfNextMonth)
        return delay.toMillis()
    }
}
