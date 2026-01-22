# Task 10: Excel File Generation

## Overview
Generate Excel files (.xlsx) containing work hours summary for a given month. The file will be attached to emails sent to recipients.

## Requirements

### Excel File Format

**Columns:**
1. **Date** - Date of the work day (format: DD/MM/YYYY or similar)
2. **Day of Week** - Day name (Monday, Tuesday, etc.)
3. **Start-End** - Time sessions, comma-separated if multiple (format: "08:00-11:00, 13:30-20:15")
4. **Total Hours** - Total hours worked (format: X.Y hours or decimal)
5. **Comment** - Day type comment (day-off/holiday eve/holiday) or empty

**Rows:**
- One row per day in the month
- Only include days from first day to last day of month
- **No future days** - Only include up to today
- Include all days, even if no work was done (show empty/zero)

### Excel File Details
- File format: .xlsx (Excel 2007+)
- Sheet name: "Work Hours" or month name
- Headers in first row (bold)
- Data rows below
- Auto-size columns (optional but nice)
- Professional formatting

## Implementation Steps

### Step 1: Add Apache POI Dependency
- Add POI library for Android
- Use lightweight version if possible
- Handle Android compatibility

### Step 2: Create Excel Generator Service
- `ExcelGeneratorService.kt`
- Function to generate Excel for a month
- Accept month/year and work days data
- Return File or ByteArray

### Step 3: Implement Excel Creation
- Create workbook and sheet
- Create header row
- Create data rows for each day
- Format cells (dates, numbers, text)
- Auto-size columns

### Step 4: Format Time Entries
- Format multiple sessions: "08:00-11:00, 13:30-20:15"
- Handle empty days (no entries)
- Format total hours as decimal or "X.Y"

### Step 5: Handle Day Types
- Format comment column
- "day-off" for Day Off
- "holiday eve" for Holiday Evening
- "holiday" for Holiday
- Empty for Normal days

### Step 6: Date Formatting
- Format dates consistently
- Format day of week names
- Handle locale considerations

### Step 7: File Storage
- Save to app's cache or files directory
- Generate unique filename (e.g., "work_hours_2024_01.xlsx")
- Clean up old files (optional)

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── data/
│   └── excel/
│       └── ExcelGeneratorService.kt
```

## Excel Generation Logic

### Data Preparation
```kotlin
data class ExcelRow(
    val date: LocalDate,
    val dayOfWeek: String,
    val sessions: String,        // "08:00-11:00, 13:30-20:15"
    val totalHours: Double,
    val comment: String          // "day-off", "holiday eve", "holiday", or ""
)

fun prepareExcelData(
    workDays: List<WorkDay>,
    month: YearMonth
): List<ExcelRow> {
    // Get all days in month (first to last)
    // Match with work days data
    // Format sessions
    // Calculate totals
    // Format comments
}
```

### Session Formatting
```kotlin
fun formatSessions(entries: List<TimeEntry>): String {
    if (entries.isEmpty()) return ""
    return entries.joinToString(", ") { entry ->
        "${entry.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))}-" +
        "${entry.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
    }
}
```

## Apache POI Usage

### Create Workbook
```kotlin
val workbook = XSSFWorkbook()
val sheet = workbook.createSheet("Work Hours")

// Create header row
val headerRow = sheet.createRow(0)
headerRow.createCell(0).setCellValue("Date")
headerRow.createCell(1).setCellValue("Day of Week")
// ... etc

// Create data rows
data.forEachIndexed { index, rowData ->
    val dataRow = sheet.createRow(index + 1)
    dataRow.createCell(0).setCellValue(rowData.date.toString())
    // ... etc
}

// Auto-size columns
sheet.autoSizeColumn(0)
// ... etc
```

## File Handling

### Save File
```kotlin
fun generateExcelFile(
    month: YearMonth,
    workDays: List<WorkDay>
): File {
    val workbook = createWorkbook(month, workDays)
    val file = File(context.cacheDir, "work_hours_${month.year}_${month.monthValue}.xlsx")
    FileOutputStream(file).use { workbook.write(it) }
    return file
}
```

## Dependencies to Add
```toml
poi = "5.2.5"
```

```kotlin
implementation("org.apache.poi:poi:5.2.5")
implementation("org.apache.poi:poi-ooxml:5.2.5")
```

**Note:** POI can be large. Consider alternatives:
- `poi-android` (if available)
- Or use CSV format (simpler, but less professional)
- Or use a lighter Excel library

## Alternative: CSV Format
If POI is too large, CSV is simpler:
- Same data structure
- Comma-separated values
- Can be opened in Excel
- Much smaller library

## Error Handling
- Handle file creation errors
- Handle write errors
- Validate data before generation
- Return null/error on failure

## Testing Considerations
- Test with various month scenarios
- Test with empty months
- Test with multiple sessions
- Test day type formatting
- Test date formatting
- Test file creation and reading

## File Cleanup
- Delete old Excel files periodically
- Or keep last N files
- Or delete after email sent (if not needed)

## Performance
- Generate on background thread
- Show loading indicator
- Cache generated files? (probably not needed)
