package com.example.kriptogan.data.database.converters

import androidx.room.TypeConverter
import com.example.kriptogan.data.model.DayType

class DayTypeConverter {
    @TypeConverter
    fun fromDayType(dayType: DayType): String {
        return dayType.name
    }

    @TypeConverter
    fun toDayType(dayType: String): DayType {
        return DayType.valueOf(dayType)
    }
}
