package domain.model

data class AgendaDay(
    val agendaMonthYear: AgendaMonthYear,
    val dayInWeek: DayInWeek,
    val dayOfMonth: Int,
    val epochDay: Long
)