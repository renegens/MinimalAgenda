package domain.use_case

import data.calendar.toEvent
import domain.model.AgendaEvent
import domain.model.DayListViewType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import repository.CalendarRepository
import repository.HiddenEventsRepository

class GetAllEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val hiddenEventsRepository: HiddenEventsRepository,
    private val flatCalendarViewFactory: FlatCalendarViewFactory
) {

    sealed class State {
        class Success(
            val agendaEvents: List<AgendaEvent>,
            val dayListViewTypes: List<DayListViewType>
        ) : State()
    }

    suspend operator fun invoke(): State {
        val allEvents = calendarRepository.getAllEvents().map { it.toEvent() }
        val hiddenEvents = hiddenEventsRepository.getEvents()
        val timeOffset = getTimeZoneOffsetInSeconds()

        // Create a lookup set of hidden event IDs by epochDay for faster checks
        val hiddenEventIdsByEpochDay = hiddenEvents.groupBy { it.epochDay }
            .mapValues { entry -> entry.value.map { it.eventId }.toSet() }  // Set for O(1) lookups

        // Iterate through events and update them in a single loop
        allEvents.forEach { agendaEvent ->
            val epochDay = secondsToEpochDays(agendaEvent.startTime, timeOffset)
            agendaEvent.epochDay = epochDay

            // Check if the event is hidden by looking up in the set
            agendaEvent.isHidden = hiddenEventIdsByEpochDay[epochDay]?.contains(agendaEvent.id) == true
        }

        val data = flatCalendarViewFactory.build(allEvents)

        return State.Success(agendaEvents = allEvents, dayListViewTypes = data)
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