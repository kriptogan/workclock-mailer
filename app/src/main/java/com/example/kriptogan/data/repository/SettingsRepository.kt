package com.example.kriptogan.data.repository

import com.example.kriptogan.data.database.dao.AppSettingsDao
import com.example.kriptogan.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

class SettingsRepository(
    private val appSettingsDao: AppSettingsDao
) {
    suspend fun getSettings(): AppSettings? {
        return appSettingsDao.getSettings()
    }

    fun getSettingsFlow(): Flow<AppSettings?> {
        return appSettingsDao.getSettingsFlow()
    }

    suspend fun updateSettings(settings: AppSettings) {
        // Use insert with REPLACE strategy to handle both insert and update cases
        appSettingsDao.insertSettings(settings)
    }

    suspend fun initializeDefaultSettings() {
        val existing = appSettingsDao.getSettings()
        if (existing == null) {
            val defaultSettings = AppSettings(
                id = 1,
                workDays = setOf(
                    DayOfWeek.SUNDAY,
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY
                ),
                workHoursPerDay = 9,
                breakMinutes = 30,
                holidayEveningHours = 6,
                semiDayOffHours = 4
            )
            appSettingsDao.insertSettings(defaultSettings)
        }
    }
}
