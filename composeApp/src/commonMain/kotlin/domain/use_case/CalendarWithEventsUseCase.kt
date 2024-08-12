package domain.use_case

import data.EventEntity
import domain.model.AgendaDay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import repository.CalendarRepository

class CalendarWithEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val calendarGenerator: CalendarGenerator
) {

    private var emptyCalendar: List<AgendaDay>? = null

    sealed class State {
        class Success(val days: List<AgendaDay>, val events: List<EventEntity>): State()
    }

    suspend operator fun invoke(): State {

        if (emptyCalendar == null) {
            emptyCalendar = calendarGenerator()
        }

        val allEvents = calendarRepository.getAllEvents()
        println(Clock.System.now().epochSeconds)

        val timeOffset = getTimeZoneOffsetInSeconds()
        allEvents.map { it.epochDay = secondsToEpochDays(it.startTime!!, timeOffset) }

        return State.Success(days = emptyCalendar!!, events = allEvents)
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
