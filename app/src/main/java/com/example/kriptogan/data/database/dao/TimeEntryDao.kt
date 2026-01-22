package com.example.kriptogan.data.database.dao

import androidx.room.*
import com.example.kriptogan.data.model.TimeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntryDao {
    @Query("SELECT * FROM time_entries WHERE workDayId = :workDayId ORDER BY startTime ASC")
    fun getTimeEntriesForWorkDay(workDayId: Long): Flow<List<TimeEntry>>

    @Query("SELECT * FROM time_entries WHERE workDayId = :workDayId ORDER BY startTime ASC")
    suspend fun getTimeEntriesForWorkDaySync(workDayId: Long): List<TimeEntry>

    @Insert
    suspend fun insertTimeEntry(timeEntry: TimeEntry): Long

    @Update
    suspend fun updateTimeEntry(timeEntry: TimeEntry)

    @Delete
    suspend fun deleteTimeEntry(timeEntry: TimeEntry)

    @Query("DELETE FROM time_entries WHERE id = :id")
    suspend fun deleteTimeEntryById(id: Long)

    @Query("DELETE FROM time_entries WHERE workDayId = :workDayId")
    suspend fun deleteTimeEntriesForWorkDay(workDayId: Long)
}
