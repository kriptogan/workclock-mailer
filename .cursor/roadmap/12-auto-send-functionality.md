# Task 12: Auto-Send Functionality

## Overview
Implement automatic email sending at the end of each month. Use WorkManager to schedule and execute the send operation.

## Requirements

### Auto-Send Features

1. **Toggle Enable/Disable**
   - Already in email config screen
   - User can enable/disable auto-send
   - Default: disabled (manual only)

2. **Scheduled Execution**
   - Trigger at end of month
   - Generate Excel for completed month
   - Send email automatically
   - No user interaction required

3. **Timing**
   - When to trigger: Last day of month? First day of next month?
   - Recommendation: First day of next month (ensures month is complete)
   - Time: Early morning (e.g., 2:00 AM) to avoid interfering with user

4. **Error Handling**
   - If send fails, log error
   - Option to retry
   - Notify user of failure? (optional)

5. **User Notification**
   - Show notification when email is sent
   - Or silent operation (user preference?)

## Implementation Steps

### Step 1: Add WorkManager Dependency
- Add WorkManager library
- Set up periodic work

### Step 2: Create Auto-Send Worker
- `AutoSendWorker.kt`
- Extend CoroutineWorker
- Check if auto-send is enabled
- Generate Excel for previous month
- Send email
- Handle errors

### Step 3: Schedule Periodic Work
- Schedule work to run monthly
- Use PeriodicWorkRequest
- Set constraints (network required)
- Set initial delay

### Step 4: Update Email Config
- When auto-send is enabled, schedule work
- When disabled, cancel work
- Update work when config changes

### Step 5: Handle Month Boundaries
- Check if it's the first day of month
- Generate report for previous month
- Send email

### Step 6: Add Notification (Optional)
- Show notification on success
- Show notification on failure
- User can disable notifications

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── data/
│   └── work/
│       └── AutoSendWorker.kt
├── ui/
│   └── emailconfig/
│       └── EmailConfigViewModel.kt (update to schedule work)
```

## WorkManager Implementation

### Create Worker
```kotlin
class AutoSendWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Check if auto-send is enabled
            val emailConfig = emailConfigRepository.getEmailConfig()
            if (!emailConfig.autoSendEnabled) {
                return Result.success() // Not enabled, skip
            }
            
            // Check if it's first day of month
            val today = LocalDate.now()
            if (today.dayOfMonth != 1) {
                return Result.success() // Not first day, skip
            }
            
            // Get previous month
            val previousMonth = today.minusMonths(1)
            
            // Generate Excel
            val workDays = workDayRepository.getWorkDaysForMonth(
                previousMonth.year,
                previousMonth.monthValue
            )
            val excelFile = excelGeneratorService.generateExcelFile(
                YearMonth.of(previousMonth.year, previousMonth.monthValue),
                workDays
            )
            
            // Send email
            emailService.sendEmail(
                recipients = emailConfig.recipients,
                subject = emailConfig.subject ?: "Work Hours Report",
                body = emailConfig.body ?: "",
                excelFile = excelFile
            )
            
            // Show notification (optional)
            showNotification("Monthly report sent successfully")
            
            Result.success()
        } catch (e: Exception) {
            // Log error
            Log.e("AutoSendWorker", "Failed to send auto email", e)
            Result.retry() // Retry on failure
        }
    }
}
```

### Schedule Work
```kotlin
fun scheduleAutoSend(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    
    val workRequest = PeriodicWorkRequestBuilder<AutoSendWorker>(
        1, TimeUnit.MONTHS  // Run monthly
    )
        .setConstraints(constraints)
        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
        .build()
    
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "auto_send_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
}

fun cancelAutoSend(context: Context) {
    WorkManager.getInstance(context)
        .cancelUniqueWork("auto_send_work")
}
```

### Calculate Initial Delay
```kotlin
fun calculateInitialDelay(): Long {
    val now = LocalDateTime.now()
    val firstOfNextMonth = now
        .plusMonths(1)
        .withDayOfMonth(1)
        .withHour(2)  // 2 AM
        .withMinute(0)
        .withSecond(0)
    
    val delay = Duration.between(now, firstOfNextMonth)
    return delay.toMillis()
}
```

## Integration with Email Config

### When Auto-Send Enabled
```kotlin
fun enableAutoSend() {
    emailConfigRepository.updateAutoSend(true)
    scheduleAutoSend(context)
}
```

### When Auto-Send Disabled
```kotlin
fun disableAutoSend() {
    emailConfigRepository.updateAutoSend(false)
    cancelAutoSend(context)
}
```

## Notification (Optional)

### Show Success Notification
```kotlin
fun showNotification(message: String) {
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Work Hours Report")
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_notification)
        .build()
    
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
}
```

## Error Handling

### Retry Logic
- WorkManager automatically retries on failure
- Can set backoff policy
- Max retries: 3-5 times

### Failure Notification
- Show notification if all retries fail
- Allow user to manually send

## Testing Considerations
- Test work scheduling
- Test work execution on first of month
- Test skip when not first of month
- Test skip when auto-send disabled
- Test error handling and retry
- Test notification display

## Dependencies to Add
```toml
workmanager = "2.10.0"
```

```kotlin
implementation("androidx.work:work-runtime-ktx:$workmanager")
```

## Permissions
- Already have internet permission
- Need notification permission (Android 13+)
- Add to manifest

## User Preferences
- Allow user to choose notification preference
- Silent mode option
- Custom send time (future enhancement)

## Edge Cases
- What if user changes email config after scheduling?
- What if month has no data?
- What if email sending fails?
- What if app is uninstalled/reinstalled?

## Future Enhancements
- Custom send time
- Send to different recipients per month
- Monthly summary in notification
- Preview before sending (even for auto-send)
