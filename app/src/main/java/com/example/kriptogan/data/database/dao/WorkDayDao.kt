package com.example.kriptogan.data.database.dao

import androidx.room.*
import com.example.kriptogan.data.model.WorkDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkDayDao {
    @Query("SELECT * FROM work_days WHERE date = :date LIMIT 1")
    suspend fun getWorkDayByDate(date: LocalDate): WorkDay?

    @Query("SELECT * FROM work_days WHERE date = :date LIMIT 1")
    fun getWorkDayByDateFlow(date: LocalDate): Flow<WorkDay?>

    @Query("""
        SELECT * FROM work_days 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date ASC
    """)
    fun getWorkDaysInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkDay>>

    @Query("""
        SELECT * FROM work_days 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date ASC
    """)
    suspend fun getWorkDaysInDateRangeSync(startDate: LocalDate, endDate: LocalDate): List<WorkDay>

    @Query("""
        SELECT DISTINCT 
            CAST(strftime('%Y', date/86400000 + 2440588, 'unixepoch') AS INTEGER) as year,
            CAST(strftime('%m', date/86400000 + 2440588, 'unixepoch') AS INTEGER) as month
        FROM work_days
        ORDER BY year DESC, month DESC
    """)
    suspend fun getMonthsWithData(): List<YearMonth>

    @Query("""
        SELECT * FROM work_days
        WHERE CAST(strftime('%Y', date/86400000 + 2440588, 'unixepoch') AS INTEGER) = :year
        AND CAST(strftime('%m', date/86400000 + 2440588, 'unixepoch') AS INTEGER) = :month
        ORDER BY date ASC
    """)
    suspend fun getWorkDaysForMonth(year: Int, month: Int): List<WorkDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkDay(workDay: WorkDay): Long

    @Update
    suspend fun updateWorkDay(workDay: WorkDay)

    @Delete
    suspend fun deleteWorkDay(workDay: WorkDay)

    @Query("DELETE FROM work_days WHERE id = :id")
    suspend fun deleteWorkDayById(id: Long)
}

data class YearMonth(val year: Int, val month: Int)
