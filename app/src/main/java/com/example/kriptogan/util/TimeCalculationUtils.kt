package com.example.kriptogan.util

import com.example.kriptogan.data.model.AppSettings
import com.example.kriptogan.data.model.DayType
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.data.model.WorkDay
import com.example.kriptogan.data.repository.WorkDayWithEntries
import java.time.Duration
import java.time.LocalTime

object TimeCalculationUtils {
    
    data class DailyCalculation(
        val totalWorked: Double,      // hours
        val expected: Int,             // hours
        val overtime: Double,          // hours (0 if negative)
        val missing: Double            // hours (0 if negative)
    )

    data class MonthlyCalculation(
        val totalWorked: Double,
        val totalExpected: Int,
        val totalOvertime: Double,
        val totalMissing: Double,
        val netOvertime: Double,      // positive if overtime
        val netMissing: Double        // positive if missing
    )
    fun calculateNetHours(timeEntry: TimeEntry): Double {
        val start = timeEntry.startTime
        val end = timeEntry.endTime
        val duration = if (end.isAfter(start)) {
            Duration.between(start, end)
        } else {
            Duration.between(start, LocalTime.MAX) + Duration.between(LocalTime.MIN, end)
        }
        val totalMinutes = duration.toMinutes() - timeEntry.breakMinutes
        return totalMinutes / 60.0
    }

    fun getExpectedHours(workDay: WorkDay, settings: AppSettings): Int {
        return when (workDay.dayType) {
            DayType.HOLIDAY -> 0
            DayType.DAY_OFF -> 0
            DayType.HOLIDAY_EVENING -> settings.holidayEveningHours
            DayType.SEMI_DAY_OFF -> settings.semiDayOffHours
            DayType.NORMAL -> {
                if (workDay.date.dayOfWeek in settings.workDays) {
                    settings.workHoursPerDay
                } else {
                    0 // Weekend
                }
            }
        }
    }

    fun calculateDaily(workDayWithEntries: WorkDayWithEntries, settings: AppSettings): DailyCalculation {
        val totalWorked = workDayWithEntries.timeEntries.sumOf { 
            calculateNetHours(it) 
        }
        val expected = getExpectedHours(workDayWithEntries.workDay, settings)
        val difference = totalWorked - expected
        
        return DailyCalculation(
            totalWorked = totalWorked,
            expected = expected,
            overtime = if (difference > 0) difference else 0.0,
            missing = if (difference < 0) -difference else 0.0
        )
    }

    fun calculateMonthly(
        workDaysWithEntries: List<WorkDayWithEntries>,
        settings: AppSettings
    ): MonthlyCalculation {
        val dailyCalcs = workDaysWithEntries.map { calculateDaily(it, settings) }
        
        val totalOvertime = dailyCalcs.sumOf { it.overtime }
        val totalMissing = dailyCalcs.sumOf { it.missing }
        val net = totalOvertime - totalMissing
        
        return MonthlyCalculation(
            totalWorked = dailyCalcs.sumOf { it.totalWorked },
            totalExpected = dailyCalcs.sumOf { it.expected },
            totalOvertime = totalOvertime,
            totalMissing = totalMissing,
            netOvertime = if (net > 0) net else 0.0,
            netMissing = if (net < 0) -net else 0.0
        )
    }

    fun formatHours(hours: Double): String {
        val wholeHours = hours.toInt()
        val minutes = ((hours - wholeHours) * 60).toInt()
        return if (minutes > 0) {
            "${wholeHours}h ${minutes}m"
        } else {
            "${wholeHours}h"
        }
    }
}
