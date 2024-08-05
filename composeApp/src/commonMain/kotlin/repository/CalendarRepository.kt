package repository

import data.CalendarData
import data.EventData

expect interface CalendarRepository {
    suspend fun getCalendarsAndroid(): List<CalendarData>
    fun getCalendarsiOS(): List<CalendarData>
    suspend fun getEventsAndroid(calendarId: Long): List<EventData>
}