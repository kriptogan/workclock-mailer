# Task 8: Time Calculations & Overtime Logic

## Overview
Implement comprehensive time calculations showing overtime/missing time per day and monthly summary. Overtime and missing time offset each other in the total.

## Requirements

### Daily Calculations

For each day, calculate:
1. **Total Hours Worked**
   - Sum of all time entries (net hours after break deduction)
   - Format: X.Y hours

2. **Expected Hours**
   - Based on day type and settings:
     - Normal work day: Settings work hours
     - Holiday: 0 hours
     - Holiday Evening: Settings holiday evening hours
     - Day Off: 0 hours
     - Weekend: 0 hours (if not work day)

3. **Overtime/Missing Time**
   - Overtime = Worked - Expected (if positive)
   - Missing = Expected - Worked (if positive)
   - Display both (one will be 0)

### Monthly Summary

1. **Total Hours Worked**
   - Sum of all days in month (up to today only)

2. **Total Expected Hours**
   - Sum of expected hours for all days in month (up to today)

3. **Total Overtime**
   - Sum of all daily overtime values

4. **Total Missing Time**
   - Sum of all daily missing time values

5. **Net Overtime/Missing**
   - Net = Total Overtime - Total Missing Time
   - If positive: Net Overtime
   - If negative: Net Missing Time (display as positive)
   - These offset each other

### Display Requirements

1. **Per Day Display**
   - Show on day card in calendar
   - Show in day detail screen
   - Color coding:
     - Overtime: Green
     - Missing: Red
     - Exact: Neutral

2. **Monthly Summary Display**
   - Prominent card/section
   - Large numbers
   - Clear labels
   - Color coding for net value

## Implementation Steps

### Step 1: Create Calculation Utilities
- `TimeCalculationUtils.kt`
- Functions for daily calculations
- Functions for monthly calculations
- Expected hours calculation (considering day type)

### Step 2: Update Day Detail ViewModel
- Calculate daily totals
- Calculate overtime/missing for day
- Update when entries change

### Step 3: Update Calendar ViewModel
- Calculate monthly totals
- Calculate overtime/missing per day
- Calculate net overtime/missing
- Filter future days (only up to today)

### Step 4: Create Calculation Display Components
- `DailyCalculationDisplay.kt`
- `MonthlySummaryDisplay.kt`
- Format numbers nicely
- Color coding

### Step 5: Integrate Displays
- Add to day detail screen
- Add to calendar screen
- Update when data changes

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── domain/
│   └── TimeCalculationUtils.kt
├── ui/
│   ├── calendar/
│   │   └── components/
│   │       └── MonthlySummaryDisplay.kt
│   └── daydetail/
│       └── components/
│           └── DailyCalculationDisplay.kt
```

## Calculation Logic

### Daily Calculation
```kotlin
data class DailyCalculation(
    val totalWorked: Double,      // hours
    val expected: Int,             // hours
    val overtime: Double,          // hours (0 if negative)
    val missing: Double            // hours (0 if negative)
)

fun calculateDaily(day: WorkDay, settings: AppSettings): DailyCalculation {
    val totalWorked = day.timeEntries.sumOf { 
        calculateNetHours(it) 
    }
    val expected = getExpectedHours(day, settings)
    val difference = totalWorked - expected
    
    return DailyCalculation(
        totalWorked = totalWorked,
        expected = expected,
        overtime = if (difference > 0) difference else 0.0,
        missing = if (difference < 0) -difference else 0.0
    )
}
```

### Monthly Calculation
```kotlin
data class MonthlyCalculation(
    val totalWorked: Double,
    val totalExpected: Int,
    val totalOvertime: Double,
    val totalMissing: Double,
    val netOvertime: Double,      // positive if overtime
    val netMissing: Double        // positive if missing
)

fun calculateMonthly(
    workDays: List<WorkDay>,
    settings: AppSettings
): MonthlyCalculation {
    val dailyCalcs = workDays.map { calculateDaily(it, settings) }
    
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
```

## Display Formatting

### Time Display
- Hours: "X.Y h" or "Xh Ym"
- Overtime: "+X.Y h" (green)
- Missing: "-X.Y h" (red)
- Exact: "X.Y h" (neutral)

### Summary Display
- Large, readable numbers
- Clear labels
- Color-coded values
- Net value prominently displayed

## Edge Cases
- Empty month (no entries): Show 0s
- All holidays: Expected = 0
- Mixed day types: Calculate correctly
- Future days: Exclude from calculations
- Partial month: Only calculate up to today

## Performance Considerations
- Cache calculations
- Recalculate only when data changes
- Use efficient sum operations
- Consider large datasets (many months)

## Testing Considerations
- Test daily calculations for each day type
- Test monthly calculations
- Test overtime/missing offset
- Test edge cases (empty, all holidays, etc.)
- Test with various time entry scenarios

## Dependencies
- No new dependencies needed
- Use existing Kotlin standard library
