# Task 4: Main Calendar View & Navigation

## Overview
Create the main calendar view showing the current month with work days, time entries, and navigation structure. This is the primary interface users interact with daily.

## Requirements

### Main Calendar View

1. **Current Month Display**
   - Show calendar grid for current month
   - Highlight today's date
   - Show day of week headers
   - Display work day indicators
   - Show day type indicators (holiday, holiday evening, day off)

2. **Day Cards**
   - Each day as a card/button
   - Display date and day name
   - Show total hours worked (if any)
   - Show overtime/missing time indicator
   - Visual indicators for day types
   - Tap to open day detail/edit screen

3. **Month Navigation**
   - Previous/Next month buttons
   - Current month/year display
   - Only allow navigation to current month or past months (no future)

4. **Summary Section**
   - Monthly total hours worked
   - Total overtime (positive)
   - Total missing time (negative)
   - Net overtime/missing (overtime - missing)
   - Only calculate for days up to today (no future days)

### Navigation Structure

1. **Bottom Navigation** (or Drawer)
   - Home (Calendar)
   - Settings
   - Email Config
   - History (past months)

2. **Day Detail Screen**
   - Opens when tapping a day
   - Shows all time entries for that day
   - Add/edit/delete time entries
   - Set day type
   - Timer controls

## UI/UX Design

### Calendar Grid Layout
- 7 columns (days of week)
- Rows for weeks in month
- Card-based design for each day
- Color coding:
  - Today: Accent color border
  - Work days: Default background
  - Weekends: Lighter background
  - Holiday: Red tint
  - Holiday Evening: Orange tint
  - Day Off: Blue tint

### Day Card Components
- Date number (large)
- Day name abbreviation (small)
- Total hours badge (if > 0)
- Overtime indicator (green) or Missing time indicator (red)
- Day type icon (if set)

### Summary Card
- Prominent display at top or bottom
- Large numbers for totals
- Color-coded (green for overtime, red for missing)

## Implementation Steps

### Step 1: Set Up Navigation
- Add Navigation Compose dependency
- Create navigation graph
- Set up bottom navigation or drawer
- Define routes for all screens

### Step 2: Create Calendar ViewModel
- `CalendarViewModel.kt`
- Load work days for current month
- Calculate totals and overtime
- Handle month navigation
- Filter out future days

### Step 3: Create Calendar Screen
- `CalendarScreen.kt`
- Calendar grid layout
- Day cards composable
- Month navigation controls
- Summary section

### Step 4: Create Day Card Component
- `DayCard.kt`
- Reusable composable for each day
- Handle tap to navigate to day detail
- Display day information

### Step 5: Create Summary Component
- `MonthlySummary.kt`
- Calculate totals from work days
- Display in card format

### Step 6: Create Main Activity Navigation
- Update `MainActivity.kt`
- Set up navigation host
- Configure bottom navigation/drawer

### Step 7: Date Utilities
- `DateUtils.kt`
- Get days in month
- Get first/last day of month
- Format dates
- Check if date is in future

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── ui/
│   ├── navigation/
│   │   ├── NavigationGraph.kt
│   │   └── NavRoutes.kt
│   ├── calendar/
│   │   ├── CalendarScreen.kt
│   │   ├── CalendarViewModel.kt
│   │   └── components/
│   │       ├── DayCard.kt
│   │       ├── CalendarGrid.kt
│   │       └── MonthlySummary.kt
│   └── daydetail/
│       └── DayDetailScreen.kt (placeholder for next task)
├── util/
│   └── DateUtils.kt
```

## State Management
- ViewModel holds month state
- LiveData/StateFlow for work days list
- Computed values for totals

## Calculations Needed
- Total hours worked in month (sum of all time entries, minus breaks)
- Expected hours (based on work days, holidays, holiday evenings)
- Overtime per day and total
- Missing time per day and total
- Net overtime/missing (offset each other)

## Date Handling
- Use `java.time.LocalDate` for dates
- Handle month boundaries correctly
- Only show/calculate up to today
- Handle timezone considerations (use device timezone)

## Dependencies to Add
```toml
navigation = "2.8.4"
viewmodel = "2.10.0"
```

```kotlin
implementation("androidx.navigation:navigation-compose:$navigation")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$viewmodel")
implementation("androidx.lifecycle:lifecycle-runtime-compose:$viewmodel")
```

## Testing Considerations
- Test calendar grid rendering
- Test month navigation
- Test calculations for various scenarios
- Test day card tap navigation
- Test summary calculations
