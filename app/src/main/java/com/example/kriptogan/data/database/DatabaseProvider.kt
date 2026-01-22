package com.example.kriptogan.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: WorkClockDatabase? = null

    fun getDatabase(context: Context): WorkClockDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                WorkClockDatabase::class.java,
                "work_clock_database"
            )
                .fallbackToDestructiveMigration() // For development - remove in production
                .addMigrations(MIGRATION_1_2)
            .build()
            INSTANCE = instance
            instance
        }
    }
}
