# Task 5: Timer Functionality

## Overview
Implement a start/stop timer with manual correction option. Users can track work time in real-time and adjust entries if needed.

## Requirements

### Timer Features

1. **Start/Stop Timer**
   - Start button begins tracking time
   - Stop button ends current session
   - Display elapsed time while running
   - Multiple sessions per day (each start/stop creates a new entry)

2. **Timer Display**
   - Show current elapsed time (HH:MM:SS format)
   - Update every second while running
   - Show start time
   - Show current time (if running)

3. **Manual Correction**
   - After stopping, allow editing start/end times
   - Time picker dialogs for start and end
   - Save corrected entry
   - Delete entry option

4. **Break Time Deduction**
   - Automatically apply break time deduction from settings
   - Show break time in display
   - Option to override break time for this entry

5. **Multiple Sessions**
   - Can start multiple timers throughout the day
   - Each session creates a separate TimeEntry
   - View all sessions for the day
   - Total time calculation includes all sessions

## UI/UX Design

### Timer Controls
- Large, prominent start/stop button
- Circular progress indicator (optional)
- Elapsed time display (large text)
- Current session info card

### Timer States
1. **Idle**: Show start button, no active timer
2. **Running**: Show stop button, elapsed time counting up
3. **Stopped**: Show edit/delete options, final time displayed

### Manual Correction Dialog
- Time picker for start time
- Time picker for end time
- Break time override input
- Save and Cancel buttons

## Implementation Steps

### Step 1: Create Timer Service/Manager
- `TimerManager.kt` or use ViewModel
- Track current session state
- Calculate elapsed time
- Handle start/stop logic

### Step 2: Create Timer ViewModel
- `DayDetailViewModel.kt` (or separate TimerViewModel)
- Manage timer state
- Handle time entry creation
- Update work day with new entries

### Step 3: Create Timer UI Component
- `TimerControls.kt`
- Start/stop button
- Elapsed time display
- Session info display

### Step 4: Create Time Correction Dialog
- `TimeCorrectionDialog.kt`
- Time pickers for start/end
- Break time input
- Validation and save logic

### Step 5: Integrate with Day Detail Screen
- Add timer section to day detail
- Show active timer if running
- Show list of completed sessions

### Step 6: Background Timer Handling
- Use foreground service or ViewModel with lifecycle awareness
- Handle app backgrounding (pause timer or continue)
- Persist timer state

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── ui/
│   ├── timer/
│   │   ├── TimerControls.kt
│   │   ├── TimerViewModel.kt
│   │   └── TimeCorrectionDialog.kt
│   └── daydetail/
│       └── DayDetailScreen.kt (uses timer)
├── domain/
│   └── TimerManager.kt (optional)
```

## State Management
- Timer state: Idle, Running, Stopped
- Current session: start time, elapsed time
- Use `remember` and coroutines for timer updates

## Timer Logic
- Start: Record current time, set state to Running
- Stop: Record end time, create TimeEntry, set state to Idle
- Elapsed: Current time - start time (while running)
- Update UI every second using `LaunchedEffect` or `DisposableEffect`

## Break Time Application
- When stopping timer, apply break deduction from settings
- Calculate: (end time - start time) - break minutes
- Store break minutes in TimeEntry
- Allow manual override in correction dialog

## Background Handling
- Option 1: Pause timer when app goes to background (simpler)
- Option 2: Continue timer in background (requires foreground service)
- Recommendation: Start with Option 1, can enhance later

## Edge Cases
- What if user closes app while timer running? (Save state, restore on reopen)
- What if device time changes? (Use system time, handle gracefully)
- What if timer runs past midnight? (Create entry for current day, handle separately)

## Dependencies
- Already have Compose and ViewModel
- May need time picker library or custom implementation

## Testing Considerations
- Test start/stop functionality
- Test elapsed time calculation
- Test multiple sessions
- Test manual correction
- Test break time deduction
- Test background/foreground transitions
