package domain.model

import data.DayInWeek
import data.EventEntity

data class AgendaDay(
    val agendaMonthYear: AgendaMonthYear,
    val dayInWeek: DayInWeek,
    val dayOfMonth: Int,
    val epochDay: Int,
    var events: List<EventEntity>
)