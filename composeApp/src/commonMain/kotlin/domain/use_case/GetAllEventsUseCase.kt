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
            calendarRepository.getEvents(durationInMonths).map { it.toEvent() }
        }

        val hiddenEvents = hiddenEventsRepository.getEvents()

        // Create a mutable list to hold expanded events
        val expandedEvents = mutableListOf<AgendaEvent>()

        allEvents.forEach { event ->
            if (event.isMultiDay) {
                // Generate an event for each day in the span
                val daysInSpan = event.startLocalDateTime.date.toEpochDays()..event.endLocalDateTime.date.toEpochDays()

                daysInSpan.forEachIndexed { index, day ->
                    val isLastDay = (index == daysInSpan.last - daysInSpan.first) // Check if it's the last day
                    val isIntermediateDay = index > 0 && !isLastDay // Any day after the start but before the last

                    val dailyEvent = when {
                        isLastDay -> event.copy(
                            epochDay = day,
                            isLastDayInMultiDayEvent = true // Mark as the subsequent/last event
                        )
                        isIntermediateDay -> event.copy(
                            epochDay = day,
                            allDay = true, // Mark as an all-day event for intermediate days
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