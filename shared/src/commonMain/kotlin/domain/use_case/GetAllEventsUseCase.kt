package domain.use_case

import data.calendar.toEvent
import domain.model.AgendaEvent
import domain.model.DayListViewType
import repository.CalendarRepository
import repository.HiddenEventsRepository

class GetAllEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val hiddenEventsRepository: HiddenEventsRepository,
    private val flatCalendarViewFactory: FlatCalendarViewFactory,
    private val showDemoModeUseCase: ShouldShowDemoModeUseCase
) {

    sealed class State {
        class Success(
            val agendaEvents: List<AgendaEvent>, val dayListViewTypes: List<DayListViewType>
        ) : State()
    }

    suspend operator fun invoke(durationInMonths: Int): State {

        val allEvents = if (showDemoModeUseCase()){
            generateDemoAgendaEvents()
        }else{
            calendarRepository.getEvents(durationInMonths).mapNotNull { it.toEvent() }
        }

        val hiddenEvents = hiddenEventsRepository.getEvents()

        // Create a mutable list to hold expanded events
        val expandedEvents = mutableListOf<AgendaEvent>()

        allEvents.forEach { event ->
            if (event.isMultiDay) {
                // Generate an event for each day in the span
                val firstDay = event.startLocalDateTime.date.toEpochDays()
                val lastDay = event.endLocalDateTime.date.toEpochDays()
                // A provider can return a decades-long event overlapping the query. Expand only visible days.
                val daysInSpan = maxOf(firstDay, flatCalendarViewFactory.coveredDays.first)..
                    minOf(lastDay, flatCalendarViewFactory.coveredDays.last)

                daysInSpan.forEach { day ->
                    val isLastDay = day == lastDay
                    val isIntermediateDay = day > firstDay && !isLastDay

                    val dailyEvent = when {
                        isLastDay -> event.copy(
                            epochDay = day,
                            isLastDayInMultiDayEvent = true // Mark as the subsequent/last event
                        )
                        isIntermediateDay -> event.copy(
                            epochDay = day,
                            isIntermediateDay = true,
                            isLastDayInMultiDayEvent = false // Ensure not marked as subsequent
                        )
                        else -> event.copy(
                            epochDay = day // Start day, no special flags
                        )
                    }
                    expandedEvents.add(dailyEvent)
                }
            } else {
                expandedEvents.add(event)
            }
        }

        // Now we have a full list of expanded events
        val allEventsMap = expandedEvents.associateBy { it.epochDay to it.idWithoutRID() }

        hiddenEvents.forEach { hiddenEvent ->
            val key = hiddenEvent.epochDay to hiddenEvent.idWithoutRID()
            allEventsMap[key]?.let { event ->
                event.isHidden = true
            }
        }

        val data = flatCalendarViewFactory.build(expandedEvents)
        return State.Success(agendaEvents = expandedEvents, dayListViewTypes = data)
    }
}
