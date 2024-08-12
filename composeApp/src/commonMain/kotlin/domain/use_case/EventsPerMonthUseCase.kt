package domain.use_case

import data.CalendarEntity
import data.EventEntity
import domain.model.AgendaDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.asTimeSource
import kotlinx.datetime.offsetIn
import kotlinx.datetime.toLocalDateTime
import repository.CalendarRepository

class EventsPerMonthUseCase(
    private val calendarRepository: CalendarRepository,
) {

    private var calendars: List<CalendarEntity>? = null

    suspend operator fun invoke(months: Int): List<EventEntity> {
//        if (calendars == null) {
//            calendars = calendarRepository.getCalendars()
//        }
//        val allEvents = mutableListOf<EventEntity>()
//        println(Clock.System.now().epochSeconds)
//        calendars?.forEach { calendar ->
//            val calendarEvents = calendarRepository.getEvents("" ?: "", months)
//            allEvents.addAll(calendarEvents)
//        }
//        println(Clock.System.now().epochSeconds)

        println(Clock.System.now().epochSeconds)
        val allEvents = calendarRepository.getAllEvents()
        println(Clock.System.now().epochSeconds)

        val timeOffset = getTimeZoneOffsetInSeconds()
        allEvents.map { it.epochDay = secondsToEpochDays(it.startTime!!, timeOffset) }
        println(Clock.System.now().epochSeconds)
        return allEvents


       // return allEvents
    }

    private fun timeIntervalSince1970ToEpochDay(timeIntervalSince1970: Long): Int {
        // Convert the time interval to an Instant
        val instant = Instant.fromEpochSeconds(timeIntervalSince1970)

        // Convert the Instant to a LocalDateTime in a specific time zone (UTC in this case)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Return the number of days since the epoch (1970-01-01)
        return localDateTime.date.toEpochDays()
    }

    private fun timeIntervalSince1970ToEpochDay2(timeIntervalSince1970: Long?): Int {

        if (timeIntervalSince1970 == null){
            return -1
        }

        return (timeIntervalSince1970 / 86400).toInt()
    }

    private fun secondsToEpochDays(seconds: Long, timeZoneOffsetInSeconds: Int): Int {
        // Subtract the timezone offset to convert the time to UTC
        val utcSeconds = seconds + timeZoneOffsetInSeconds
        // Convert seconds to days by dividing by the number of seconds in a day
        return (utcSeconds / (24 * 60 * 60)).toInt()
    }

    fun getTimeZoneOffsetInSeconds(): Int {
        // Get the current date-time in the system's default time zone
        return Clock.System.now().offsetIn(TimeZone.currentSystemDefault()).totalSeconds
    }
}