package repository

import data.CalendarData

expect interface CalendarRepository {
    suspend fun getCalendarsAndroid(): List<CalendarData>
    fun getCalendarsiOS(): List<CalendarData>
}