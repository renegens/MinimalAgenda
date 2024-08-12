package repository

import data.CalendarEntity
import data.EventEntity

interface CalendarRepository {
    suspend fun getCalendars(): List<CalendarEntity>
    suspend fun getEvents(calendarId: String, timeInMonths: Int): List<EventEntity>
    suspend fun getAllEvents(): List<EventEntity>
}