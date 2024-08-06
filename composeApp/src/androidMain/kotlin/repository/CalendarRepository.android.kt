package repository

import data.CalendarEntity
import data.EventEntity

actual interface CalendarRepository {
    actual suspend fun getCalendarsAndroid(): List<CalendarEntity>
    actual suspend fun getEventsAndroid(calendarId: Long): List<EventEntity>
    actual fun getCalendarsiOS(): List<CalendarEntity>
}