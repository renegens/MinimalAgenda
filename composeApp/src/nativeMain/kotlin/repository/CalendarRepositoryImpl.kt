package repository

import data.CalendarEntity
import data.EventEntity

class CalendarRepositoryImpl: CalendarRepository {
    override suspend fun getCalendarsAndroid(): List<CalendarEntity> {
        return emptyList()
    }

    override fun getCalendarsiOS(): List<CalendarEntity> {
        return emptyList()
    }

    override suspend fun getEventsAndroid(calendarId: Long): List<EventEntity> {
        return emptyList()
    }
}