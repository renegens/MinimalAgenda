package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import kotlinx.datetime.LocalDateTime

interface CalendarRepository {
    suspend fun getCalendars(): List<CalendarEntity>
    suspend fun getAllEvents(): List<EventEntity>
    suspend fun addEvent(
        title: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean = false
    ): Boolean
}