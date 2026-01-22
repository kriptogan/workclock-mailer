package com.example.kriptogan.data.database.converters

import androidx.room.TypeConverter
import java.time.DayOfWeek

class DayOfWeekSetConverter {
    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekSet(dayString: String?): Set<DayOfWeek>? {
        return dayString?.split(",")?.mapNotNull { dayName ->
            try {
                DayOfWeek.valueOf(dayName.trim())
            } catch (e: IllegalArgumentException) {
                null
            }
        }?.toSet()
    }
}
