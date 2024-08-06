package domain.model

import kotlinx.datetime.LocalDate

data class Agenda(
    val days: List<AgendaDay>,
    val months: List<AgendaMonth>,
    val years: List<AgendaYear>,
    val monthIndex: Int,
    val dayIndex: Int,
    val yearIndex: Int,
    val today: LocalDate
)