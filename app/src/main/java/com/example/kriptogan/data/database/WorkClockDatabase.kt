package com.example.kriptogan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kriptogan.data.database.converters.DayOfWeekSetConverter
import com.example.kriptogan.data.database.converters.DayTypeConverter
import com.example.kriptogan.data.database.converters.LocalDateConverter
import com.example.kriptogan.data.database.converters.LocalTimeConverter
import com.example.kriptogan.data.database.converters.StringListConverter
import com.example.kriptogan.data.database.dao.AppSettingsDao
import com.example.kriptogan.data.database.dao.EmailConfigDao
import com.example.kriptogan.data.database.dao.TimeEntryDao
import com.example.kriptogan.data.database.dao.WorkDayDao
import com.example.kriptogan.data.model.AppSettings
import com.example.kriptogan.data.model.EmailConfig
import com.example.kriptogan.data.model.TimeEntry
import com.example.kriptogan.data.model.WorkDay

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQLite doesn't support adding NOT NULL columns directly, so we need to recreate the table
        // Step 1: Create new table with the new column
        database.execSQL("""
            CREATE TABLE app_settings_new (
                id INTEGER NOT NULL PRIMARY KEY,
                workDays TEXT NOT NULL,
                workHoursPerDay INTEGER NOT NULL,
                breakMinutes INTEGER NOT NULL,
                holidayEveningHours INTEGER NOT NULL,
                semiDayOffHours INTEGER NOT NULL DEFAULT 4
            )
        """.trimIndent())
        
        // Step 2: Copy existing data, setting semiDayOffHours to 4 for existing rows
        database.execSQL("""
            INSERT INTO app_settings_new (id, workDays, workHoursPerDay, breakMinutes, holidayEveningHours, semiDayOffHours)
            SELECT id, workDays, workHoursPerDay, breakMinutes, holidayEveningHours, 4
            FROM app_settings
        """.trimIndent())
        
        // Step 3: Drop old table
        database.execSQL("DROP TABLE app_settings")
        
        // Step 4: Rename new table to original name
        database.execSQL("ALTER TABLE app_settings_new RENAME TO app_settings")
    }
}

@Database(
    entities = [WorkDay::class, TimeEntry::class, AppSettings::class, EmailConfig::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    LocalDateConverter::class,
    LocalTimeConverter::class,
    DayOfWeekSetConverter::class,
    StringListConverter::class,
    DayTypeConverter::class
)
abstract class WorkClockDatabase : RoomDatabase() {
    abstract fun workDayDao(): WorkDayDao
    abstract fun timeEntryDao(): TimeEntryDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun emailConfigDao(): EmailConfigDao
}
