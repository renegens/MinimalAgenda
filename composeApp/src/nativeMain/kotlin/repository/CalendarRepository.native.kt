package repository

import data.CalendarData

actual interface CalendarRepository {
    actual fun getCalendars(): List<CalendarData>

}