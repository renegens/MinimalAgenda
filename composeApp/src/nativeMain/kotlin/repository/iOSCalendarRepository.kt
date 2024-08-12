package repository

import data.CalendarEntity
import data.EventEntity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDate
import platform.EventKit.*
import platform.Foundation.dateByAddingTimeInterval
import platform.Foundation.timeIntervalSince1970
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ClassName")
class iOSCalendarRepository : CalendarRepository {

    private val eventStore = EKEventStore()


    override suspend fun getCalendars(): List<CalendarEntity> {
        return suspendCancellableCoroutine { continuation ->
            eventStore.requestFullAccessToEventsWithCompletion { granted, error ->
                if (granted) {
                    val calendars =
                        eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent).map {
                            (it as EKCalendar).let { calendar ->
                                CalendarEntity(
                                    id = calendar.calendarIdentifier,
                                    name = calendar.title,
                                    accountName = calendar.description ?: "",
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
                    continuation.resumeWithException(
                        (error ?: RuntimeException("Access denied")) as Throwable
                    )
                }
            }
        }
    }

    override suspend fun getEvents(calendarId: String): List<EventEntity> {
        return suspendCancellableCoroutine { continuation ->
            val calendar = eventStore.calendarWithIdentifier(calendarId)
            val startDate = NSDate()
            val endDate = NSDate().dateByAddingTimeInterval(30.0) // 30 days from now

            val predicate =
                eventStore.predicateForEventsWithStartDate(startDate, endDate, listOf(calendar))
            val events = eventStore.eventsMatchingPredicate(predicate).map {
                (it as EKEvent).let { event ->
                    EventEntity(
                        id = event.eventIdentifier,
                        title = event.title,
                        description = null,
                        startTime = event.startDate?.timeIntervalSince1970?.toLong(),
                        endTime = event.endDate?.timeIntervalSince1970?.toLong(),
                        epochDay = timeIntervalSince1970ToEpochDay(
                            event.startDate?.timeIntervalSince1970?.toLong() ?: -1
                        )
                    )
                }
            }
            continuation.resume(events)
        }
    }

    private fun timeIntervalSince1970ToEpochDay(timeIntervalSince1970: Long): Int {
        // Convert the time interval to an Instant
        val instant = Instant.fromEpochSeconds(timeIntervalSince1970)

        // Convert the Instant to a LocalDateTime in a specific time zone (UTC in this case)
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

        // Return the number of days since the epoch (1970-01-01)
        return localDateTime.date.toEpochDays()
    }
}