package com.example.kriptogan.data.model

enum class DayType {
    NORMAL,           // Regular work day
    HOLIDAY,          // Holiday - no work expected
    HOLIDAY_EVENING,  // Holiday evening - reduced hours expected
    DAY_OFF,          // Personal day off
    SEMI_DAY_OFF      // Semi-day off - partial hours expected
}
