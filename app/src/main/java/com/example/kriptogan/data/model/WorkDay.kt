package com.example.kriptogan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "work_days")
data class WorkDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val dayType: DayType = DayType.NORMAL,
    val comment: String? = null
)
