# Task 1: Data Models & Database Setup

## Status: ✅ COMPLETED

## Overview
Set up the complete data layer including entity models, Room database, DAOs, and repositories. This is the foundation for all data persistence in the app.

## Requirements

### Data Models Needed

#### 1. TimeEntry Entity
- `id`: Long (primary key, auto-generated)
- `workDayId`: Long (foreign key to WorkDay)
- `startTime`: LocalTime (when work session started)
- `endTime`: LocalTime (when work session ended)
- `breakMinutes`: Int (break time deducted, default 0)

#### 2. WorkDay Entity
- `id`: Long (primary key, auto-generated)
- `date`: LocalDate (the date of this work day)
- `dayType`: DayType enum (NORMAL, HOLIDAY, HOLIDAY_EVENING, DAY_OFF)
- `timeEntries`: List<TimeEntry> (one-to-many relationship)
- `comment`: String? (optional comment)

#### 3. AppSettings Entity (Singleton)
- `id`: Long (primary key, always 1)
- `workDays`: Set<DayOfWeek> (which days are work days, default: Sun-Thu)
- `workHoursPerDay`: Int (hours expected per work day, default: 9)
- `breakMinutes`: Int (default break time deduction, default: 30)
- `holidayEveningHours`: Int (hours expected on holiday evenings, default: 6)

#### 4. EmailConfig Entity (Singleton)
- `id`: Long (primary key, always 1)
- `senderEmail`: String? (Gmail account email)
- `recipients`: List<String> (list of recipient emails)
- `subject`: String? (email subject line)
- `body`: String? (email body text)
- `autoSendEnabled`: Boolean (whether to auto-send at month end, default: false)
- `accessToken`: String? (OAuth2 access token, encrypted)
- `refreshToken`: String? (OAuth2 refresh token, encrypted)

### Enums

#### DayType
```kotlin
enum class DayType {
    NORMAL,           // Regular work day
    HOLIDAY,          // Holiday - no work expected
    HOLIDAY_EVENING,  // Holiday evening - reduced hours expected
    DAY_OFF           // Personal day off
}
```

## Implementation Steps

### Step 1: Add Dependencies
Update `gradle/libs.versions.toml` and `app/build.gradle.kts`:
- Room runtime, compiler, ktx
- Type converters for LocalDate, LocalTime, Set<DayOfWeek>

### Step 2: Create Type Converters
- `LocalDateConverter`: Convert LocalDate to/from Long (epoch days)
- `LocalTimeConverter`: Convert LocalTime to/from Long (minutes since midnight)
- `DayOfWeekSetConverter`: Convert Set<DayOfWeek> to/from String (comma-separated)
- `StringListConverter`: Convert List<String> to/from String (comma-separated)

### Step 3: Create Entity Classes
- `TimeEntry.kt` with Room annotations
- `WorkDay.kt` with Room annotations and @Relation to TimeEntry
- `AppSettings.kt` with Room annotations
- `EmailConfig.kt` with Room annotations

### Step 4: Create DAOs
- `TimeEntryDao.kt`: CRUD operations for time entries
- `WorkDayDao.kt`: CRUD operations for work days, queries by date range
- `AppSettingsDao.kt`: Get/Update settings (singleton pattern)
- `EmailConfigDao.kt`: Get/Update email config (singleton pattern)

### Step 5: Create Database Class
- `WorkClockDatabase.kt`: Room database with all entities and DAOs
- Include type converters
- Migration strategy for future schema changes

### Step 6: Create Repositories
- `WorkDayRepository.kt`: Business logic for work days
- `SettingsRepository.kt`: Business logic for settings
- `EmailConfigRepository.kt`: Business logic for email config

### Step 7: Initialize Default Settings
Create a database initialization function that sets default values:
- Work days: Sunday-Thursday
- Work hours: 9
- Break time: 30 minutes
- Holiday evening hours: 6

## File Structure
```
app/src/main/java/com/example/kriptogan/
├── data/
│   ├── database/
│   │   ├── WorkClockDatabase.kt
│   │   ├── converters/
│   │   │   ├── LocalDateConverter.kt
│   │   │   ├── LocalTimeConverter.kt
│   │   │   ├── DayOfWeekSetConverter.kt
│   │   │   └── StringListConverter.kt
│   │   └── dao/
│   │       ├── TimeEntryDao.kt
│   │       ├── WorkDayDao.kt
│   │       ├── AppSettingsDao.kt
│   │       └── EmailConfigDao.kt
│   ├── model/
│   │   ├── TimeEntry.kt
│   │   ├── WorkDay.kt
│   │   ├── AppSettings.kt
│   │   ├── EmailConfig.kt
│   │   └── DayType.kt
│   └── repository/
│       ├── WorkDayRepository.kt
│       ├── SettingsRepository.kt
│       └── EmailConfigRepository.kt
```

## Testing Considerations
- Test type converters with various edge cases
- Test DAO operations (insert, update, delete, query)
- Test repository logic
- Test default settings initialization

## Dependencies to Add
```toml
room = "2.6.1"
```

```kotlin
implementation("androidx.room:room-runtime:$room")
implementation("androidx.room:room-ktx:$room")
kapt("androidx.room:room-compiler:$room")
```

## Implementation Status

### ✅ Completed Components

1. **Dependencies Added**
   - ✅ Room runtime, ktx, and compiler
   - ✅ Navigation Compose
   - ✅ ViewModel & Lifecycle Compose
   - ✅ Kapt plugin for annotation processing

2. **Data Models Created**
   - ✅ `DayType.kt` - Enum with 4 types (NORMAL, HOLIDAY, HOLIDAY_EVENING, DAY_OFF)
   - ✅ `TimeEntry.kt` - Entity with start/end times and break minutes
   - ✅ `WorkDay.kt` - Entity with date, day type, and comment
   - ✅ `AppSettings.kt` - Singleton entity for app configuration
   - ✅ `EmailConfig.kt` - Singleton entity for email settings

3. **Type Converters Created**
   - ✅ `LocalDateConverter.kt` - Converts LocalDate ↔ Long (epoch days)
   - ✅ `LocalTimeConverter.kt` - Converts LocalTime ↔ Long (seconds)
   - ✅ `DayOfWeekSetConverter.kt` - Converts Set<DayOfWeek> ↔ String
   - ✅ `StringListConverter.kt` - Converts List<String> ↔ String
   - ✅ `DayTypeConverter.kt` - Converts DayType ↔ String

4. **DAOs Created**
   - ✅ `TimeEntryDao.kt` - Full CRUD with Flow support
   - ✅ `WorkDayDao.kt` - Date range queries, month/year extraction
   - ✅ `AppSettingsDao.kt` - Singleton pattern implementation
   - ✅ `EmailConfigDao.kt` - Singleton pattern implementation

5. **Database & Infrastructure**
   - ✅ `WorkClockDatabase.kt` - Room database with all entities and converters
   - ✅ `DatabaseProvider.kt` - Singleton database instance provider
   - ✅ Database version 1 with migration strategy

6. **Repositories Created**
   - ✅ `WorkDayRepository.kt` - Business logic with WorkDayWithEntries data class
   - ✅ `SettingsRepository.kt` - Settings management with default initialization
   - ✅ `EmailConfigRepository.kt` - Email config with helper methods

7. **Default Initialization**
   - ✅ Default settings: Sun-Thu, 9 hours, 30 min break, 6h holiday evening
   - ✅ Default email config initialization

### Files Created
- 5 Entity/Model files
- 5 Type Converter files
- 4 DAO files
- 1 Database file
- 1 Database Provider file
- 3 Repository files

**Total: 19 files created**

### Next Steps
Ready to proceed with Task 2: Settings Screen Implementation
