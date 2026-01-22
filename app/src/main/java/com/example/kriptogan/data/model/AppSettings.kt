package com.example.kriptogan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Long = 1, // Always 1 for singleton
    val workDays: Set<DayOfWeek>, // Which days are work days
    val workHoursPerDay: Int, // Hours expected per work day
    val breakMinutes: Int, // Default break time deduction in minutes
    val holidayEveningHours: Int, // Hours expected on holiday evenings
    val semiDayOffHours: Int = 4 // Hours expected on semi-day off (default 4 hours)
)
