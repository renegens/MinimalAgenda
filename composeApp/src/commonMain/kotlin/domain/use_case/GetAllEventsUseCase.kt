package domain.use_case

import data.calendar.toEvent
import domain.model.AgendaEvent
import domain.model.DayListViewType
import repository.CalendarRepository
import repository.HiddenEventsRepository

class GetAllEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val hiddenEventsRepository: HiddenEventsRepository,
    private val flatCalendarViewFactory: FlatCalendarViewFactory
) {

    sealed class State {
        class Success(
            val agendaEvents: List<AgendaEvent>, val dayListViewTypes: List<DayListViewType>
        ) : State()
    }

    suspend operator fun invoke(durationInMonths: Int): State {
        val allEvents = calendarRepository.getEvents(durationInMonths).map { it.toEvent() }
        val hiddenEvents = hiddenEventsRepository.getEvents()

        val allEventsMap = allEvents.associateBy { it.epochDay to it.idWithoutRID() }

        hiddenEvents.forEach { hiddenEvent ->
            val key = hiddenEvent.epochDay to hiddenEvent.idWithoutRID()
            allEventsMap[key]?.let { event ->
                event.isHidden = true
            }
        }

        val data = flatCalendarViewFactory.build(allEvents)
        return State.Success(agendaEvents = allEvents, dayListViewTypes = data)
    }
}