package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import data.calendar.EventTimeRange
import data.calendar.CalendarMath
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import platform.EventKit
import platform.EventKit.EKCalendar
import platform.EventKit.EKEntityType
import platform.EventKit.EKEvent
import platform.EventKit.EKSpan
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.toEKEventDate
import platform.uses24HourClock
import kotlin.time.ExperimentalTime

@Suppress("ClassName")
@OptIn(ExperimentalTime::class)
class iOSCalendarRepository(eventKit: EventKit) : CalendarRepository {

    private val eventStore = eventKit.eventStore

    // Permission requests belong to CalendarPermissionHandler (including the pre-iOS-17 API).
    override suspend fun getCalendars(): List<CalendarEntity> = withContext(Dispatchers.IO) {
        eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent).map { value ->
            val calendar = value as EKCalendar
            CalendarEntity(
                id = calendar.calendarIdentifier,
                name = calendar.title,
                accountName = "",
                accountType = calendar.type.name,
                canWrite = calendar.allowsContentModifications,
                isPrimary = calendar.calendarIdentifier == eventStore.defaultCalendarForNewEvents()?.calendarIdentifier,
            )
        }
    }

    // iOS always has a local calendar; nothing to create.
    override suspend fun createLocalCalendar(): Boolean = false

    private fun nsStartDateForCalendar(months: Int): NSDate {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val threeYearsAgo = calendar.dateByAddingUnit(
            NSCalendarUnitMonth, value = months * (-1).toLong(), toDate = currentDate, options = 0u
        )
        return threeYearsAgo ?: NSDate()  // Fallback to current date if something goes wrong
    }

    private fun nsEndDateForCalendar(months: Int): NSDate {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val threeYearsInFuture = calendar.dateByAddingUnit(
            NSCalendarUnitMonth, value = months.toLong(), toDate = currentDate, options = 0u
        )
        return threeYearsInFuture ?: NSDate()
    }

    override suspend fun getEvent(id: String): EventEntity? = withContext(Dispatchers.IO) {
        eventStore.eventWithIdentifier(id)?.toEntity(TimeZone.currentSystemDefault(), uses24HourClock())
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun addEvent(
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
        calendarId: String?,
        timedRange: EventTimeRange?,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val calendar = if (calendarId == null) eventStore.defaultCalendarForNewEvents()
                else eventStore.calendarWithIdentifier(calendarId)
            if (calendar == null || !calendar.allowsContentModifications) return@withContext false
            val newEvent = EKEvent.eventWithEventStore(eventStore)
            newEvent.calendar = calendar
            newEvent.allDay = allDay
            newEvent.title = title
            newEvent.location = location
            newEvent.startDate = if (!allDay && timedRange != null)
                NSDate.dateWithTimeIntervalSince1970(timedRange.start.epochSeconds.toDouble())
            else startTime.toEKEventDate()
            newEvent.endDate = if (!allDay && timedRange != null)
                NSDate.dateWithTimeIntervalSince1970(timedRange.end.epochSeconds.toDouble())
            else (if (allDay) CalendarMath.allDayEndExclusive(endTime) else endTime).toEKEventDate()
            // EventKit returns false (with NSError), rather than throwing, when saving fails.
            eventStore.saveEvent(newEvent, EKSpan.EKSpanThisEvent, true, null)
        }
    }

    // iOS edits and deletes through Apple's native editor (EKEventEditViewController).
    override suspend fun updateEvent(
        id: String,
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
        timedRange: EventTimeRange?,
    ): Boolean = false

    override suspend fun deleteEvent(id: String): Boolean = false

    override suspend fun getEvents(durationInMonths: Int): List<EventEntity> = withContext(Dispatchers.IO) {
        val timeZone = TimeZone.currentSystemDefault()
        val clock24 = uses24HourClock()
        val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
        val predicate = eventStore.predicateForEventsWithStartDate(
            nsStartDateForCalendar(durationInMonths), nsEndDateForCalendar(durationInMonths), calendars,
        )
        eventStore.eventsMatchingPredicate(predicate).map { (it as EKEvent).toEntity(timeZone, clock24) }
    }

    private fun EKEvent.toEntity(zone: TimeZone, uses24HourClock: Boolean): EventEntity {
        val startMillis = ((startDate?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
        val endMillis = ((endDate?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
        val times = CalendarMath.resolveLocalCalendarTimes(startMillis, endMillis, allDay, zone)
        return EventEntity(
            id = eventIdentifier,
            title = title,
            location = location,
            startTime = times.startSeconds,
            endTime = times.endSeconds,
            allDay = allDay,
            epochDay = times.epochDay,
            isRecurring = recurrenceRules().isNullOrEmpty().not(),
            startLocalDateTime = times.startLocalDateTime,
            endLocalDateTime = times.endLocalDateTime,
            isMultiDay = times.isMultiDay,
            uses24HourClock = uses24HourClock,
        )
    }
}
