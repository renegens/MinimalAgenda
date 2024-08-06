package repository

import data.CalendarEntity
import data.EventEntity

expect interface CalendarRepository {
    suspend fun getCalendarsAndroid(): List<CalendarEntity>
    fun getCalendarsiOS(): List<CalendarEntity>
    suspend fun getEventsAndroid(calendarId: Long): List<EventEntity>
}