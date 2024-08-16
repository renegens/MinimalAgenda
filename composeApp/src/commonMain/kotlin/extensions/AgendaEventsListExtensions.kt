package extensions

import domain.model.AgendaEvent
import domain.model.AgendaMonthYear

private fun List<AgendaEvent>.hasEventsForDay(epochDay: Int): Boolean {
    return this.any { it.epochDay!! == epochDay }
}

private fun List<AgendaEvent>.hasHiddenEventsForDay(epochDay: Int): Boolean {
    return this.filter { it.epochDay == epochDay }.any { it.isHidden.not() }
}

fun List<AgendaEvent>.hasEvents(epochDay: Int, showAll: Boolean): Boolean {
    return if (showAll) {
        this.hasEventsForDay(epochDay)
    } else {
        this.hasHiddenEventsForDay(epochDay)
    }
}

fun List<AgendaEvent>.getInRange(
    agendaMonthYear: AgendaMonthYear
): List<AgendaEvent> {
    return this.filter { event -> agendaMonthYear.isEpochDayInRange(event.epochDay!!) }
}

fun List<AgendaEvent>.today(epochDay: Int): List<AgendaEvent>{
    return this.filter { it.epochDay == epochDay }
}