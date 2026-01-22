# Task 7: Day Type Management (Holiday/Holiday Evening/Day Off)

## Overview
Allow users to mark days as different types (holiday, holiday evening, day off) which affects expected work hours and calculations.

## Requirements

### Day Types

1. **Normal Day** (default)
   - Regular work day
   - Expected hours: From settings (default 9 hours)
   - Can have time entries

2. **Holiday**
   - No work expected
   - Expected hours: 0
   - Can still have time entries (if worked)
   - Visual indicator: Red tint/icon

3. **Holiday Evening**
   - Reduced work expected
   - Expected hours: From settings (default 6 hours)
   - Can have time entries
   - Visual indicator: Orange tint/icon

4. **Day Off**
   - Personal day off
   - Expected hours: 0
   - Can still have time entries (if worked)
   - Visual indicator: Blue tint/icon
   - Usually limited amount per employee

### Day Type Selection
- Easy way to set day type
- Visual indicators on calendar
- Can change day type anytime
- Clear indication of current type

## UI/UX Design

### Day Type Selector
- In day detail screen
- Radio buttons or chips for each type
- Icons for each type
- Description of each type
- Current selection highlighted

### Visual Indicators
- Calendar: Color-coded day cards
- Day detail: Header with type indicator
- Icons:
  - Holiday: 🎉 or calendar icon (red)
  - Holiday Evening: 🌙 or moon icon (orange)
  - Day Off: 🏖️ or vacation icon (blue)
  - Normal: No special icon

### Day Type Descriptions
- Show expected hours for each type
- Explain what each type means
- Show how it affects calculations

## Implementation Steps

### Step 1: Update Day Detail Screen
- Add day type selector section
- Display current day type
- Allow changing day type

### Step 2: Create Day Type Selector Component
- `DayTypeSelector.kt`
- Radio group or chip group
- Icons and labels
- Handle selection

### Step 3: Update Calendar View
- Show day type indicators on day cards
- Color coding
- Icons

### Step 4: Update WorkDay Entity
- Already has `dayType` field
- Ensure it's properly saved/loaded

### Step 5: Update Calculations
- Consider day type when calculating expected hours
- Holiday: 0 expected
- Holiday Evening: Settings value
- Day Off: 0 expected
- Normal: Settings value (if work day) or 0 (if weekend)

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── ui/
│   ├── daydetail/
│   │   └── components/
│   │       └── DayTypeSelector.kt
│   └── calendar/
│       └── components/
│           └── DayCard.kt (update to show type)
```

## Calculation Logic

### Expected Hours by Day Type
```kotlin
fun getExpectedHours(day: WorkDay, settings: AppSettings): Int {
    return when (day.dayType) {
        DayType.HOLIDAY -> 0
        DayType.DAY_OFF -> 0
        DayType.HOLIDAY_EVENING -> settings.holidayEveningHours
        DayType.NORMAL -> {
            if (day.date.dayOfWeek in settings.workDays) {
                settings.workHoursPerDay
            } else {
                0 // Weekend
            }
        }
    }
}
```

## User Flow
1. User opens day detail screen
2. Sees current day type (default: Normal)
3. Taps day type selector
4. Chooses new type
5. Day type is saved
6. Calendar view updates to show indicator
7. Calculations update based on new type

## Visual Design
- Use Material3 color system
- Holiday: Error/Red color
- Holiday Evening: Warning/Orange color
- Day Off: Info/Blue color
- Normal: Default color

## Edge Cases
- Changing day type after entries exist (recalculate expected hours)
- Weekend marked as holiday (still 0 expected, but shows as holiday)
- Work day marked as day off (0 expected, but can still log time)

## Testing Considerations
- Test setting each day type
- Test visual indicators
- Test calculation updates
- Test persistence
- Test calendar display

## Dependencies
- Material Icons Extended (for icons)
- Already have DayType enum
