package repository

import data.calendar.CalendarEntity
import data.calendar.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.EventKit
import platform.Foundation.NSDate
import platform.EventKit.*
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.timeIntervalSince1970
import kotlin.coroutines.resume

@Suppress("ClassName")
class iOSCalendarRepository(eventKit: EventKit) : CalendarRepository {

    private val eventStore = eventKit.eventStore

    override suspend fun getCalendars(): List<CalendarEntity> {
        return withContext(Dispatchers.Default) {
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

    override suspend fun getEvents(calendarId: String, timeInMonths: Int): List<EventEntity> {
        return withContext(Dispatchers.Default) {

            suspendCancellableCoroutine { continuation ->
                val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
                val startDate = nsStartDateForCalendar(timeInMonths)
                val endDate = nsEndDateForCalendar(timeInMonths)

                val predicate =
                    eventStore.predicateForEventsWithStartDate(startDate, endDate, calendars)
                val events = eventStore.eventsMatchingPredicate(predicate).map { event ->
                    event as EKEvent
                    EventEntity(
                        id = event.eventIdentifier,
                        title = event.title,
                        startTime = event.startDate?.timeIntervalSince1970?.toLong(),
                    )
                }

                continuation.resume(events)
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
        return threeYearsInFuture ?: NSDate()  // Fallback to current date if something goes wrong
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
                        startTime = event.startDate?.timeIntervalSince1970?.toLong(),
                        epochDay = null,
                    )
                }

                continuation.resume(events)
            }
        }
    }
}