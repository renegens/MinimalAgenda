package repository

import data.CalendarData

actual interface CalendarRepository {
    actual suspend fun getCalendarsAndroid(): List<CalendarData>
    actual fun getCalendarsiOS(): List<CalendarData>
}