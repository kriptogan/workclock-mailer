# Task 9: Historical Months View & Management

## Overview
Create a view to browse, view, edit, and resend reports for past months. Users can access historical data and regenerate Excel reports.

## Requirements

### Historical Months View

1. **Months List**
   - List of past months (most recent first)
   - Show month name and year
   - Show summary stats (total hours, net overtime/missing)
   - Only show months with data (or all months?)
   - Tap to view/edit month

2. **Month Detail View**
   - Similar to current month calendar view
   - Show all days of that month
   - Allow editing time entries
   - Allow changing day types
   - Show monthly summary

3. **Resend Email**
   - Button to regenerate Excel and send email
   - Uses current email configuration
   - Confirmation dialog
   - Success/error feedback

4. **Edit Capabilities**
   - Full edit access to past months
   - Add/edit/delete time entries
   - Change day types
   - All same functionality as current month

## UI/UX Design

### Months List Screen
- List of month cards
- Each card shows:
  - Month name and year (e.g., "January 2024")
  - Total hours worked
  - Net overtime/missing
  - Visual indicator (color-coded)
- Empty state if no historical data
- Search/filter (optional, future enhancement)

### Month Card Design
- Large month/year header
- Summary stats in card
- "View" or "Edit" button
- Visual summary (progress bar or similar)

### Month Detail Screen
- Same as current month calendar view
- Read-only indicator? (No, fully editable)
- "Resend Email" button in toolbar
- Navigation back to months list

## Implementation Steps

### Step 1: Create Historical Months ViewModel
- `HistoricalMonthsViewModel.kt`
- Load list of months with data
- Calculate summaries for each month
- Handle month selection

### Step 2: Create Months List Screen
- `HistoricalMonthsScreen.kt`
- Display list of months
- Handle month selection
- Show summaries

### Step 3: Create Month Card Component
- `MonthCard.kt`
- Display month info
- Show summary stats
- Handle tap to view

### Step 4: Reuse Calendar View for Past Months
- Make CalendarScreen reusable
- Accept month parameter
- Allow editing (same as current month)
- Show "Resend Email" button

### Step 5: Add Resend Email Functionality
- Button in month detail toolbar
- Generate Excel for that month
- Send email using current config
- Show loading/success/error states

### Step 6: Update Navigation
- Add historical months route
- Add month detail route with month parameter
- Navigation from months list to month detail

### Step 7: Query Historical Data
- Update WorkDayRepository
- Query by month/year
- Get months with data
- Efficient queries

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── ui/
│   ├── history/
│   │   ├── HistoricalMonthsScreen.kt
│   │   ├── HistoricalMonthsViewModel.kt
│   │   └── components/
│   │       └── MonthCard.kt
│   └── calendar/
│       └── CalendarScreen.kt (make reusable)
```

## Data Queries

### Get Months with Data
```kotlin
@Query("""
    SELECT DISTINCT 
        strftime('%Y', date/1000, 'unixepoch') as year,
        strftime('%m', date/1000, 'unixepoch') as month
    FROM work_days
    ORDER BY year DESC, month DESC
""")
suspend fun getMonthsWithData(): List<YearMonth>
```

### Get Work Days for Month
```kotlin
@Query("""
    SELECT * FROM work_days
    WHERE strftime('%Y', date/1000, 'unixepoch') = :year
    AND strftime('%m', date/1000, 'unixepoch') = :month
    ORDER BY date ASC
""")
suspend fun getWorkDaysForMonth(year: Int, month: Int): List<WorkDay>
```

## Month Identification
- Use `YearMonth` or custom data class
- Format: "January 2024"
- Sort: Most recent first

## Resend Email Flow
1. User taps "Resend Email" in month detail
2. Show confirmation dialog
3. Generate Excel for that month
4. Send email using current email config
5. Show success message
6. Handle errors gracefully

## Edit Capabilities
- Same as current month
- No restrictions on past months
- All CRUD operations available
- Recalculate summaries after edits

## Performance Considerations
- Lazy load month summaries
- Cache calculations
- Efficient database queries
- Pagination if many months (future)

## Edge Cases
- No historical data: Show empty state
- Month with no entries: Still show in list?
- Deleted all entries: Remove from list or show empty?
- Very old months: Handle date formatting

## Testing Considerations
- Test months list display
- Test month selection
- Test editing past months
- Test resend email
- Test with various data scenarios
- Test empty states

## Dependencies
- No new dependencies
- Reuse existing components
