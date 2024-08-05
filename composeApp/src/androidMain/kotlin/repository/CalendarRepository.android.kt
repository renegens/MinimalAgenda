package repository

import data.CalendarData
import data.EventData

actual interface CalendarRepository {
    actual suspend fun getCalendarsAndroid(): List<CalendarData>
    actual suspend fun getEventsAndroid(calendarId: Long): List<EventData>
    actual fun getCalendarsiOS(): List<CalendarData>
}