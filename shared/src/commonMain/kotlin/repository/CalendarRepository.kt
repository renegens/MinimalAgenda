package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import kotlinx.datetime.LocalDateTime

interface CalendarRepository {
    suspend fun getCalendars(): List<CalendarEntity>
    /** Creates a calendar that lives only on this device. Returns true on success (always false on iOS). */
    suspend fun createLocalCalendar(): Boolean
    suspend fun getEvents(durationInMonths: Int = 24): List<EventEntity>
    suspend fun getEvent(id: String): EventEntity?
    suspend fun addEvent(
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean = false,
        /** Target calendar (an id from [getCalendars]); null = the default one. */
        calendarId: String? = null,
    ): Boolean

    /**
     * Updates the event [id] (an `EventEntity.id`). For all-day events [startTime]'s date is the first day
     * and [endTime]'s date the last day (inclusive). Recurring events: only title and location are changed
     * (the whole series); times are left alone so the series can't be corrupted. Returns true on success.
     */
    suspend fun updateEvent(
        id: String,
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
    ): Boolean

    /** Deletes the event [id]; for a recurring event this deletes the WHOLE series. Returns true on success. */
    suspend fun deleteEvent(id: String): Boolean
}