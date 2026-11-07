package testutil

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import data.calendar.EventTimeRange
import data.local.HiddenEventEntity
import domain.model.AgendaEvent
import domain.model.AppTheme
import domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import platform.CalendarPermissionHandler
import repository.CalendarRepository
import repository.DemoEventsRepository
import repository.HiddenEventsRepository
import repository.SettingsRepository
import kotlin.time.ExperimentalTime

class FakeCalendarRepository(
    var events: List<EventEntity> = emptyList()
) : CalendarRepository {
    var getEventsCalls = 0
    var lastDuration: Int? = null
    var calendars: List<CalendarEntity> = emptyList()
    var createLocalCalendarCalls = 0
    var createLocalCalendarResult = true
    val added = mutableListOf<SavedEvent>()
    val updated = mutableListOf<SavedEvent>()
    val deleted = mutableListOf<String>()
    var saveResult = true
    var beforeWrite: suspend () -> Unit = {}

    override suspend fun getCalendars(): List<CalendarEntity> = calendars

    override suspend fun createLocalCalendar(): Boolean {
        createLocalCalendarCalls++
        return createLocalCalendarResult
    }

    override suspend fun getEvents(durationInMonths: Int): List<EventEntity> {
        getEventsCalls++
        lastDuration = durationInMonths
        return events
    }

    override suspend fun getEvent(id: String): EventEntity? = events.firstOrNull { it.id == id }

    override suspend fun addEvent(
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
        calendarId: String?,
        timedRange: EventTimeRange?,
    ): Boolean {
        beforeWrite()
        added.add(SavedEvent(null, title, location, startTime, endTime, allDay, calendarId, timedRange))
        return saveResult
    }

    override suspend fun updateEvent(
        id: String,
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
        timedRange: EventTimeRange?,
    ): Boolean {
        beforeWrite()
        updated.add(SavedEvent(id, title, location, startTime, endTime, allDay, null, timedRange))
        return saveResult
    }

    override suspend fun deleteEvent(id: String): Boolean {
        beforeWrite()
        deleted.add(id)
        return saveResult
    }
}

data class SavedEvent(
    val id: String?,
    val title: String,
    val location: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val allDay: Boolean,
    val calendarId: String?,
    val timedRange: EventTimeRange? = null,
)

class FakeHiddenEventsRepository(
    var hidden: List<HiddenEventEntity> = emptyList()
) : HiddenEventsRepository {
    val toggled = mutableListOf<AgendaEvent>()

    override suspend fun getEvents(): List<HiddenEventEntity> = hidden
    override suspend fun hideEvent(agendaEvent: AgendaEvent) {}
    override suspend fun unhiddenEvent(agendaEvent: AgendaEvent) {}
    override suspend fun toggleEventVisibility(agendaEvent: AgendaEvent) {
        toggled.add(agendaEvent)
    }
}

class FakeDemoEventsRepository : DemoEventsRepository {
    override fun getEvents(events: List<AgendaEvent>) {}
    override fun addEvent(event: AgendaEvent) {}
}

class FakeSettingsRepository(var settings: Settings = Settings()) : SettingsRepository {
    var saved: Settings? = null
    override suspend fun getSettings(): Settings = settings
    override suspend fun saveSettings(settings: Settings) {
        saved = settings
        this.settings = settings
    }

    override suspend fun appTheme(): Flow<AppTheme> = flowOf(AppTheme.Default)
}

class FakePermissionHandler(
    var state: CalendarPermissionHandler.State
) : CalendarPermissionHandler {
    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State = state
    override fun getPermissionState(): CalendarPermissionHandler.State = state
}

/** Builds a calendar entity as the platform repositories would (epoch seconds, local date-times). */
@OptIn(ExperimentalTime::class)
fun eventEntity(
    id: String? = "e1",
    title: String? = "Title",
    start: LocalDateTime,
    end: LocalDateTime = LocalDateTime(start.date, start.time),
    allDay: Boolean? = false,
    isRecurring: Boolean? = false,
    isMultiDay: Boolean = start.date != end.date,
    location: String? = "",
    startTime: Long? = start.toInstant(TimeZone.UTC).epochSeconds,
    endTime: Long? = end.toInstant(TimeZone.UTC).epochSeconds,
) = EventEntity(
    id = id,
    title = title,
    location = location,
    startTime = startTime,
    endTime = endTime,
    allDay = allDay,
    isRecurring = isRecurring,
    epochDay = start.date.toEpochDays(),
    startLocalDateTime = start,
    endLocalDateTime = end,
    isMultiDay = isMultiDay,
    uses24HourClock = true
)

fun at(date: LocalDate, hour: Int, minute: Int = 0): LocalDateTime = date.atTime(hour, minute)

@OptIn(ExperimentalTime::class)
fun agendaEvent(
    id: String = "e1",
    title: String = "Title",
    start: LocalDateTime,
    end: LocalDateTime = start,
    allDay: Boolean = false,
    isHidden: Boolean = false,
    isMultiDay: Boolean = start.date != end.date,
    isLastDayInMultiDayEvent: Boolean = false,
    epochDay: Long? = start.date.toEpochDays(),
) = AgendaEvent(
    id = id,
    title = title,
    startTimeInUTC = start.toInstant(TimeZone.UTC).epochSeconds,
    endTimeInUTC = end.toInstant(TimeZone.UTC).epochSeconds,
    allDay = allDay,
    epochDay = epochDay,
    isHidden = isHidden,
    startLocalDateTime = start,
    endLocalDateTime = end,
    uses24HourClock = true,
    isMultiDay = isMultiDay,
    isLastDayInMultiDayEvent = isLastDayInMultiDayEvent
)
