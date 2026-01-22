package com.example.kriptogan.data.excel

import android.content.Context
import com.example.kriptogan.data.model.DayType
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.data.repository.WorkDayWithEntries
import com.example.kriptogan.util.DateUtils
import com.example.kriptogan.util.TimeCalculationUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ExcelGeneratorService(private val context: Context) {

    fun generateExcelFile(
        yearMonth: YearMonth,
        workDaysWithEntries: List<WorkDayWithEntries>
    ): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Work Hours")

        // Create header row
        val headerRow = sheet.createRow(0)
        val headerStyle = createHeaderStyle(workbook)
        
        val headers = listOf("Date", "Day of Week", "Start-End", "Total Hours", "Comment")
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.setCellStyle(headerStyle)
        }

        // Get all days in month
        val allDays = DateUtils.getDaysInMonth(yearMonth)
        val today = LocalDate.now()
        
        // Create data rows
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        allDays.forEachIndexed { index, date ->
            // Only include days up to today
            if (date.isAfter(today)) return@forEachIndexed
            
            val row = sheet.createRow(index + 1)
            val workDayWithEntries = workDaysWithEntries.find { it.workDay.date == date }
            
            // Date
            val dateCell = row.createCell(0)
            dateCell.setCellValue(date.format(dateFormatter))
            
            // Day of Week
            val dayOfWeekCell = row.createCell(1)
            dayOfWeekCell.setCellValue(DateUtils.formatDayName(date))
            
            // Start-End (sessions)
            val sessionsCell = row.createCell(2)
            val sessions = formatSessions(workDayWithEntries?.timeEntries ?: emptyList(), timeFormatter)
            sessionsCell.setCellValue(sessions)
            
            // Total Hours
            val totalHoursCell = row.createCell(3)
            val totalHours = if (workDayWithEntries != null) {
                workDayWithEntries.timeEntries.sumOf { TimeCalculationUtils.calculateNetHours(it) }
            } else {
                0.0
            }
            totalHoursCell.setCellValue(totalHours)
            
            // Comment
            val commentCell = row.createCell(4)
            val comment = getDayTypeComment(workDayWithEntries?.workDay?.dayType)
            commentCell.setCellValue(comment)
        }

        // Auto-size columns
        for (i in 0 until headers.size) {
            sheet.autoSizeColumn(i)
        }

        // Save to file
        val fileName = "work_hours_${yearMonth.year}_${yearMonth.monthValue}.xlsx"
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        return file
    }

    private fun createHeaderStyle(workbook: Workbook): CellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 12
        style.setFont(font)
        style.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        return style
    }

    private fun formatSessions(
        timeEntries: List<TimeEntry>,
        timeFormatter: DateTimeFormatter
    ): String {
        if (timeEntries.isEmpty()) return ""
        
        return timeEntries.joinToString(", ") { entry ->
            "${entry.startTime.format(timeFormatter)}-${entry.endTime.format(timeFormatter)}"
        }
    }

    private fun getDayTypeComment(dayType: DayType?): String {
        return when (dayType) {
            DayType.DAY_OFF -> "day-off"
            DayType.HOLIDAY_EVENING -> "holiday eve"
            DayType.HOLIDAY -> "holiday"
            DayType.SEMI_DAY_OFF -> "semi-day off"
            else -> ""
        }
    }
}
