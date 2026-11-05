package domain.model

import kotlinx.datetime.LocalDate

private const val SMALL_ROW = 35
private const val LARGE_ROW = 42
private const val MAX_ZEROES_TO_PREPEND = 5

fun createGridDay(agendaMonthYear: AgendaMonthYear): List<DayGrid> {
    val zerosToPrepend = agendaMonthYear.startDayOfMonth - 1
    val days = mutableListOf<DayGrid>()

    for (i in 1..zerosToPrepend) {
        days.add(DayGrid())
    }

    for (day in 1..agendaMonthYear.daysInMonth) {
        val local = LocalDate(
            year = agendaMonthYear.agendaYear.year,
            month = agendaMonthYear.agendaMonth.number,
            day = day.toInt()
        )
        days.add(DayGrid(day, local.toEpochDays()))
    }

    val totalRows = if (zerosToPrepend < MAX_ZEROES_TO_PREPEND) {
        SMALL_ROW
    } else {
        LARGE_ROW
    }

    val zeroToAppend = totalRows - days.size

    for (i in 1..zeroToAppend) {
        days.add(DayGrid())
    }
    return days
}

class DayGrid(val dayInMonth: Long = DONT_RENDER, val epochDay: Long = DONT_RENDER) {
    companion object {
        const val DONT_RENDER = -1L
    }
}