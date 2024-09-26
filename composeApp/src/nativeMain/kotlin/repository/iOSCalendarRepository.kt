package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import platform.EventKit
import platform.EventKit.EKCalendar
import platform.EventKit.EKEntityType
import platform.EventKit.EKEvent
import platform.EventKit.EKSpan
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeInterval
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.toEKEventDate
import kotlin.coroutines.resume

@Suppress("ClassName")
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

    private fun nsStartDateForCalendar(months: Int): NSDate {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val threeYearsAgo = calendar.dateByAddingUnit(
            NSCalendarUnitMonth,
            value = months * (-1).toLong(),
            toDate = currentDate,
            options = 0u
        )
        return threeYearsAgo ?: NSDate()  // Fallback to current date if something goes wrong
    }

    private fun nsEndDateForCalendar(months: Int): NSDate {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val threeYearsInFuture = calendar.dateByAddingUnit(
            NSCalendarUnitMonth,
            value = months.toLong(),
            toDate = currentDate,
            options = 0u
        )
        return threeYearsInFuture ?: NSDate()
    }

    override suspend fun getEvent(id: String): EventEntity? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val ekEvent = eventStore.eventWithIdentifier(id)
                if (ekEvent == null) {
                    continuation.resume(null)
                }
                ekEvent as EKEvent
                val event = EventEntity(
                    id = ekEvent.eventIdentifier,
                    title = ekEvent.title,
                    location = ekEvent.location,
                    startTime = ekEvent.startDate?.timeIntervalSince1970?.toLong(),
                    endTime = ekEvent.endDate?.timeIntervalSince1970?.toLong(),
                    allDay = ekEvent.allDay,
                    isRecurring = ekEvent.recurrenceRules().isNullOrEmpty().not(),
                    epochDay = null,
                    format = formatUnixTimestamp(ekEvent.startDate?.timeIntervalSince1970)
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
        allDay: Boolean
    ): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val calendar = eventStore.defaultCalendarForNewEvents()

                    if (calendar == null) {
                        continuation.resume(false)
                    }

                    val newEvent = EKEvent.eventWithEventStore(eventStore)

                    newEvent.title = title
                    newEvent.location = location
                    newEvent.startDate = startTime.toEKEventDate()
                    newEvent.endDate = endTime.toEKEventDate()
                    newEvent.allDay = allDay
                    newEvent.calendar = calendar

                    eventStore.saveEvent(
                        event = newEvent,
                        span = EKSpan.EKSpanThisEvent,
                        commit = true,
                        error = null
                    )

                    continuation.resume(true)
                } catch (e: Exception) {
                    continuation.resume(false)
                }
            }
        }
    }

    override suspend fun getAllEvents(): List<EventEntity> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
                val startDate = nsStartDateForCalendar(24)
                val endDate = nsEndDateForCalendar(24)
                val predicate =
                    eventStore.predicateForEventsWithStartDate(startDate, endDate, calendars)
                val events = eventStore.eventsMatchingPredicate(predicate).map { event ->
                    event as EKEvent
                    EventEntity(
                        id = event.eventIdentifier,
                        title = event.title,
                        location = event.location,
                        startTime = event.startDate?.timeIntervalSince1970?.toLong(),
                        endTime = event.endDate?.timeIntervalSince1970?.toLong(),
                        allDay = event.allDay,
                        epochDay = null,
                        isRecurring = event.recurrenceRules().isNullOrEmpty().not(),
                        format = formatUnixTimestamp(event.startDate?.timeIntervalSince1970)
                    )
                }

                continuation.resume(events)
            }
        }
    }

    private fun formatUnixTimestamp(unixTimestamp: NSTimeInterval?): String {
        val nsDate = NSDate(unixTimestamp!!)
        val nsDateFormatter = NSDateFormatter()

        nsDateFormatter.timeZone = NSTimeZone.localTimeZone
        nsDateFormatter.dateFormat = "HH mm"

        val localTimeString = nsDateFormatter.stringFromDate(nsDate)

        return localTimeString
    }
}