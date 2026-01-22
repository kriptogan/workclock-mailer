package com.example.kriptogan.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "time_entries",
    foreignKeys = [
        ForeignKey(
            entity = WorkDay::class,
            parentColumns = ["id"],
            childColumns = ["workDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workDayId"])]
)
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workDayId: Long,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val breakMinutes: Int = 0
)
