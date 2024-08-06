package domain.model

import data.EventEntity
import extensions.toShortName
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month

data class AgendaDay(
    val month: Month,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val year: Int,
    val epochDay: Int,
    var events: List<EventEntity>
) {
    val shortDay: String
        get() = dayOfWeek.name.toShortName()
}