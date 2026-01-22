package com.example.kriptogan.ui.navigation

object NavRoutes {
    const val CALENDAR = "calendar"
    const val CALENDAR_MONTH = "calendar/{year}/{month}"
    const val SETTINGS = "settings"
    const val EMAIL_CONFIG = "email_config"
    const val HISTORY = "history"
    const val DAY_DETAIL = "day_detail/{date}"
    
    fun dayDetail(date: Long): String = "day_detail/$date"
    fun calendarMonth(year: Int, month: Int): String = "calendar/$year/$month"
}
