package com.example.kriptogan.data.database.converters

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): Long? {
        return time?.toSecondOfDay()?.toLong()
    }

    @TypeConverter
    fun toLocalTime(seconds: Long?): LocalTime? {
        return seconds?.let { LocalTime.ofSecondOfDay(it) }
    }
}
