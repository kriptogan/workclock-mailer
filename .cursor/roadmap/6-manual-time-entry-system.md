# Task 6: Manual Time Entry System

## Overview
Allow users to manually add, edit, and delete time entries. Support multiple work sessions per day with full CRUD operations.

## Requirements

### Manual Entry Features

1. **Add Time Entry**
   - Button to add new time entry
   - Time picker for start time
   - Time picker for end time
   - Break time input (defaults to settings value)
   - Save entry

2. **Edit Time Entry**
   - Tap existing entry to edit
   - Modify start/end times
   - Modify break time
   - Save changes

3. **Delete Time Entry**
   - Delete button/icon on each entry
   - Confirmation dialog
   - Remove from database

4. **Multiple Sessions**
   - Display all sessions for the day
   - List format with start-end times
   - Total time calculation across all sessions
   - Visual separation between sessions

5. **Entry Display**
   - Show start time - end time
   - Show break time deducted
   - Show net hours worked
   - Format: "08:00 - 11:00 (Break: 30min) = 2.5h"

## UI/UX Design

### Time Entry List
- Card-based list of entries
- Each entry shows: start-end, break, net hours
- Edit button/icon
- Delete button/icon
- Empty state: "No time entries for this day"

### Add/Edit Dialog
- Full-screen dialog or bottom sheet
- Time pickers (Material3 TimePicker or custom)
- Break time input field
- Calculate and show net hours preview
- Save and Cancel buttons

### Entry Card Design
- Start time - End time (large, readable)
- Break time (smaller, secondary)
- Net hours (prominent, bold)
- Edit/Delete icons (end of card)

## Implementation Steps

### Step 1: Create Time Entry Dialog Component
- `TimeEntryDialog.kt`
- Reusable for both add and edit
- Time pickers for start/end
- Break time input
- Validation logic

### Step 2: Create Time Entry List Component
- `TimeEntryList.kt`
- Display list of entries
- Handle edit/delete actions
- Calculate and display totals

### Step 3: Create Time Entry Card Component
- `TimeEntryCard.kt`
- Individual entry display
- Edit and delete buttons
- Format time display

### Step 4: Integrate with Day Detail Screen
- Add "Add Entry" button
- Show time entry list
- Handle add/edit/delete actions

### Step 5: Update Day Detail ViewModel
- Add functions: addEntry, updateEntry, deleteEntry
- Update work day when entries change
- Recalculate totals

### Step 6: Time Calculation Utilities
- `TimeCalculationUtils.kt`
- Calculate duration between times
- Apply break deduction
- Format time display
- Sum multiple entries

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── ui/
│   ├── daydetail/
│   │   ├── components/
│   │   │   ├── TimeEntryList.kt
│   │   │   ├── TimeEntryCard.kt
│   │   │   └── TimeEntryDialog.kt
├── util/
│   └── TimeCalculationUtils.kt
```

## Validation Rules
- Start time must be before end time
- No overlapping entries validation (per requirements: no validations)
- Break time: 0 to duration
- Times within same day (handle midnight crossing if needed)

## Time Formatting
- Display: "HH:mm" (24-hour format)
- Duration: "Xh Ym" or "X.Y hours"
- Break: "X minutes" or "Xm"

## Calculations
- Duration = end time - start time (in minutes)
- Net hours = (duration - break minutes) / 60
- Total for day = sum of all net hours

## User Flow
1. User opens day detail screen
2. Sees list of existing entries (if any)
3. Taps "Add Entry" button
4. Fills in start/end times and break
5. Saves entry
6. Entry appears in list
7. Can edit by tapping entry
8. Can delete with confirmation

## Edge Cases
- Multiple entries on same day (all valid)
- Entry spanning midnight (handle as two entries or special case)
- Very short entries (< break time) - allow but show warning
- Empty day (no entries) - show empty state

## Dependencies
- Material3 TimePicker (if available) or custom time picker
- Date/time utilities

## Testing Considerations
- Test add entry
- Test edit entry
- Test delete entry
- Test multiple entries
- Test time calculations
- Test break time deduction
- Test empty state
