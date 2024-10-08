package domain.use_case

import domain.model.AgendaEvent
import domain.model.DayListViewType

class FlatCalendarViewFactory(
    private val calendarGenerator: CalendarGenerator
) {

    fun build(allEvents: List<AgendaEvent>? = null): List<DayListViewType> {
        val flatDayWithEvents = mutableListOf<DayListViewType>()

        val grouped = allEvents?.groupBy { it.epochDay }
        calendarGenerator.calendar.forEach { calendarItem ->
            flatDayWithEvents.add(DayListViewType.Header(calendarItem))

            grouped?.get(calendarItem.epochDay)?.let { events ->
                events.sortedWith(compareBy({ it.startTimeInUTC }, { it.title })).forEach { event ->
                    flatDayWithEvents.add(DayListViewType.Event(event))
                }
            }
            flatDayWithEvents.add(DayListViewType.Divider(calendarItem.epochDay))
        }

        return flatDayWithEvents
    }
}