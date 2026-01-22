package com.example.kriptogan.data.repository

import com.example.kriptogan.data.database.dao.TimeEntryDao
import com.example.kriptogan.data.database.dao.WorkDayDao
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.data.model.WorkDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WorkDayRepository(
    private val workDayDao: WorkDayDao,
    private val timeEntryDao: TimeEntryDao
) {
    suspend fun getWorkDayByDate(date: LocalDate): WorkDay? {
        return workDayDao.getWorkDayByDate(date)
    }

    fun getWorkDayByDateFlow(date: LocalDate): Flow<WorkDay?> {
        return workDayDao.getWorkDayByDateFlow(date)
    }

    fun getWorkDaysInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkDayWithEntries>> {
        return workDayDao.getWorkDaysInDateRange(startDate, endDate).map { workDays ->
            workDays.map { workDay ->
                val entries = timeEntryDao.getTimeEntriesForWorkDaySync(workDay.id)
                WorkDayWithEntries(workDay, entries)
            }
        }
    }

    suspend fun getWorkDaysInDateRangeSync(startDate: LocalDate, endDate: LocalDate): List<WorkDayWithEntries> {
        val workDays = workDayDao.getWorkDaysInDateRangeSync(startDate, endDate)
        return workDays.map { workDay ->
            val entries = timeEntryDao.getTimeEntriesForWorkDaySync(workDay.id)
            WorkDayWithEntries(workDay, entries)
        }
    }

    suspend fun getWorkDaysForMonth(year: Int, month: Int): List<WorkDayWithEntries> {
        val workDays = workDayDao.getWorkDaysForMonth(year, month)
        return workDays.map { workDay ->
            val entries = timeEntryDao.getTimeEntriesForWorkDaySync(workDay.id)
            WorkDayWithEntries(workDay, entries)
        }
    }

    suspend fun insertOrUpdateWorkDay(workDay: WorkDay): Long {
        val existing = workDayDao.getWorkDayByDate(workDay.date)
        return if (existing != null) {
            workDayDao.updateWorkDay(workDay.copy(id = existing.id))
            existing.id
        } else {
            workDayDao.insertWorkDay(workDay)
        }
    }

    suspend fun insertTimeEntry(timeEntry: TimeEntry): Long {
        return timeEntryDao.insertTimeEntry(timeEntry)
    }

    suspend fun updateTimeEntry(timeEntry: TimeEntry) {
        timeEntryDao.updateTimeEntry(timeEntry)
    }

    suspend fun deleteTimeEntry(timeEntry: TimeEntry) {
        timeEntryDao.deleteTimeEntry(timeEntry)
    }

    suspend fun deleteTimeEntryById(id: Long) {
        timeEntryDao.deleteTimeEntryById(id)
    }

    suspend fun deleteWorkDay(workDay: WorkDay) {
        workDayDao.deleteWorkDay(workDay)
    }

    suspend fun getMonthsWithData(): List<com.example.kriptogan.data.database.dao.YearMonth> {
        return workDayDao.getMonthsWithData()
    }
}

data class WorkDayWithEntries(
    val workDay: WorkDay,
    val timeEntries: List<TimeEntry>
)
