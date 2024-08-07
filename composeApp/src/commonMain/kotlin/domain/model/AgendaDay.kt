package domain.model

import data.EventEntity
import extensions.toShortName
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month

data class AgendaDay(
    val agendaMonthYear: AgendaMonthYear,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val epochDay: Int,
    var events: List<EventEntity>
) {
    val shortDay: String
        get() = dayOfWeek.name.toShortName()
}