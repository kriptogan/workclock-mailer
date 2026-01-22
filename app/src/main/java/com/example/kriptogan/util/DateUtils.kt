package com.example.kriptogan.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object DateUtils {
    fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        return firstDay.datesUntil(lastDay.plusDays(1)).toList()
    }

    fun getFirstDayOfWeek(yearMonth: YearMonth): LocalDate {
        val firstDay = yearMonth.atDay(1)
        val dayOfWeek = firstDay.dayOfWeek
        val daysToSubtract = (dayOfWeek.value % 7)
        return firstDay.minusDays(daysToSubtract.toLong())
    }

    fun getCalendarDays(yearMonth: YearMonth): List<LocalDate> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        
        // Get first day of week for the calendar grid
        val firstCalendarDay = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value % 7).toLong())
        
        // Get last day of week for the calendar grid
        val lastCalendarDay = lastDayOfMonth.plusDays((6 - (lastDayOfMonth.dayOfWeek.value % 7)).toLong())
        
        return firstCalendarDay.datesUntil(lastCalendarDay.plusDays(1)).toList()
    }

    fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }

    fun formatMonthYear(yearMonth: YearMonth): String {
        return yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    fun formatDayName(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("EEE"))
    }

    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }

    fun isFuture(date: LocalDate): Boolean {
        return date.isAfter(LocalDate.now())
    }

    fun dateToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }

    fun epochDayToDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }
}
