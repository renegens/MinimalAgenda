package domain.use_case

import data.calendar.toEvent
import data.local.HiddenEventEntity
import domain.model.AgendaEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import repository.CalendarRepository
import repository.HiddenEventsRepository

class GetAllEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val hiddenEventsRepository: HiddenEventsRepository
) {

    sealed class State {
        class Success(
            val agendaEvents: List<AgendaEvent>,
        ) : State()
    }

    suspend operator fun invoke(): State {
        val allEvents = calendarRepository.getAllEvents().map { it.toEvent() }
        val timeOffset = getTimeZoneOffsetInSeconds()
        val hiddenEvents = hiddenEventsRepository.getEvents()
        val groupedHiddenEvents = hiddenEvents.groupBy { it.epochDay }
        allEvents.map { agendaEvent ->
            agendaEvent.epochDay = secondsToEpochDays(agendaEvent.startTime, timeOffset)
            agendaEvent.isHidden =
                groupedHiddenEvents[agendaEvent.epochDay]?.any { it.eventId == agendaEvent.id } == true
        }
        return State.Success(agendaEvents = allEvents)
    }

    private fun secondsToEpochDays(seconds: Long, timeZoneOffsetInSeconds: Int): Int {
        // Subtract the timezone offset to convert the time to UTC
        val utcSeconds = seconds + timeZoneOffsetInSeconds
        // Convert seconds to days by dividing by the number of seconds in a day
        return (utcSeconds / (24 * 60 * 60)).toInt()
    }

    private fun getTimeZoneOffsetInSeconds(): Int {
        // Get the current date-time in the system's default time zone
        return Clock.System.now().offsetIn(TimeZone.currentSystemDefault()).totalSeconds
    }
}
