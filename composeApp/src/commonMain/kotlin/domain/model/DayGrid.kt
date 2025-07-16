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
            monthNumber = agendaMonthYear.agendaMonth.number,
            dayOfMonth = day
        )
        days.add(DayGrid(day, local.toEpochDays().toInt()))
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

class DayGrid(val dayInMonth: Int = DONT_RENDER, val epochDay: Int = DONT_RENDER) {
    companion object {
        const val DONT_RENDER = -1
    }
}