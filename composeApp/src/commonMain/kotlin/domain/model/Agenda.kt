package domain.model

data class Agenda(
    val days: List<AgendaDay>,
    val years: List<AgendaYear>,
    val yearIndex: Int
)