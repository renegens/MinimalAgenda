package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.EventKit
import platform.EventKit.EKCalendar
import platform.EventKit.EKEntityType
import platform.EventKit.EKEvent
import platform.EventKit.EKSpan
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.toEKEventDate
import platform.uses24HourClock
import kotlin.coroutines.resume
import kotlin.time.ExperimentalTime

@Suppress("ClassName")
@OptIn(ExperimentalTime::class)
class iOSCalendarRepository(eventKit: EventKit) : CalendarRepository {

    private val eventStore = eventKit.eventStore

    override suspend fun getCalendars(): List<CalendarEntity> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                eventStore.requestFullAccessToEventsWithCompletion { granted, error ->
                    if (granted) {
                        val calendars =
                            eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent).map {
                                (it as EKCalendar).let { calendar ->
                                    CalendarEntity(
                                        id = calendar.calendarIdentifier,
                                        name = calendar.title,
                                        accountName = "",
                                        accountType = calendar.type.name
                                    )

                                }
                            }

                        if (calendars.isEmpty()) {
                            continuation.resume(emptyList())
                        } else {
                            continuation.resume(calendars)
                        }

                    } else {
                        continuation.resume(emptyList())
                    }
                }
            }
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

    override suspend fun getEvent(id: String): EventEntity? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val ekEvent = eventStore.eventWithIdentifier(id)
                if (ekEvent == null) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
                val timeZone = TimeZone.currentSystemDefault()
                val uses24HourClock = uses24HourClock()
                val startLocalDateTime = Instant.fromEpochSeconds(
                    ekEvent?.startDate?.timeIntervalSince1970?.toLong() ?: 0
                ).toLocalDateTime(timeZone)

                val endLocalDateTime = Instant.fromEpochSeconds(
                    ekEvent?.endDate?.timeIntervalSince1970?.toLong() ?: 0
                ).toLocalDateTime(timeZone)
                val epochDay = startLocalDateTime.date.toEpochDays()
                val isMultiDay = isMultiDay(startEpochDay = epochDay, end = endLocalDateTime.date)

                ekEvent as EKEvent
                val event = EventEntity(
                    id = ekEvent.eventIdentifier,
                    title = ekEvent.title,
                    location = ekEvent.location,
                    startTime = ekEvent.startDate?.timeIntervalSince1970?.toLong(),
                    endTime = ekEvent.endDate?.timeIntervalSince1970?.toLong(),
                    allDay = ekEvent.allDay,
                    isRecurring = ekEvent.recurrenceRules().isNullOrEmpty().not(),
                    epochDay = epochDay,
                    startLocalDateTime = startLocalDateTime,
                    endLocalDateTime = endLocalDateTime,
                    isMultiDay = isMultiDay,
                    uses24HourClock = uses24HourClock
                )

                continuation.resume(event)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun addEvent(
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean,
        calendarId: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val calendar = eventStore.defaultCalendarForNewEvents()

                    if (calendar == null) {
                        continuation.resume(false)
                        return@suspendCancellableCoroutine
                    }

                    val newEvent = EKEvent.eventWithEventStore(eventStore)

                    newEvent.title = title
                    newEvent.location = location
                    newEvent.startDate = startTime.toEKEventDate()
                    newEvent.endDate = endTime.toEKEventDate()
                    newEvent.allDay = allDay
                    newEvent.calendar = calendar

                    eventStore.saveEvent(
                        event = newEvent, span = EKSpan.EKSpanThisEvent, commit = true, error = null
                    )

                    continuation.resume(true)
                } catch (e: Exception) {
                    continuation.resume(false)
                }
            }
        }
    }

    // iOS edits and deletes through Apple's native editor (EKEventEditViewController).
    override suspend fun updateEvent(
        id: String,
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean
    ): Boolean = false

    override suspend fun deleteEvent(id: String): Boolean = false

    override suspend fun getEvents(durationInMonths: Int): List<EventEntity> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->

                val timeZone = TimeZone.currentSystemDefault()
                val uses24HourClock = uses24HourClock()
                val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
                val startDate = nsStartDateForCalendar(durationInMonths)
                val endDate = nsEndDateForCalendar(durationInMonths)
                val predicate =
                    eventStore.predicateForEventsWithStartDate(startDate, endDate, calendars)
                val events = eventStore.eventsMatchingPredicate(predicate).map { ekEvent ->
                    ekEvent as EKEvent
                    val startLocalDateTime = Instant.fromEpochSeconds(
                        ekEvent.startDate?.timeIntervalSince1970?.toLong() ?: 0
                    ).toLocalDateTime(timeZone)

                    val endLocalDateTime = Instant.fromEpochSeconds(
                        ekEvent.endDate?.timeIntervalSince1970?.toLong() ?: 0
                    ).toLocalDateTime(timeZone)

                    val epochDay = startLocalDateTime.date.toEpochDays()
                    val isMultiDay = isMultiDay(startEpochDay = epochDay, end = endLocalDateTime.date)

                    EventEntity(
                        id = ekEvent.eventIdentifier,
                        title = ekEvent.title,
                        location = ekEvent.location,
                        startTime = ekEvent.startDate?.timeIntervalSince1970?.toLong(),
                        endTime = ekEvent.endDate?.timeIntervalSince1970?.toLong(),
                        allDay = ekEvent.allDay,
                        epochDay = epochDay,
                        isRecurring = ekEvent.recurrenceRules().isNullOrEmpty().not(),
                        startLocalDateTime = startLocalDateTime,
                        endLocalDateTime = endLocalDateTime,
                        isMultiDay = isMultiDay,
                        uses24HourClock = uses24HourClock
                    )
                }

                continuation.resume(events)
            }
        }
    }

    private fun isMultiDay(startEpochDay: Long, end: LocalDate): Boolean {
        return startEpochDay != end.toEpochDays()
    }
}