package extensions

import domain.model.AgendaEvent
import domain.model.AgendaMonth
import domain.model.AgendaMonthYear
import domain.model.AgendaYear

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

fun List<AgendaEvent>.getEventsForMonth(agendaMonth: AgendaMonth, currentYear: Int): List<AgendaEvent> {
    return this.filter { event ->
        val (month, year) = getMonthAndYearFromUnixTimestamp(event.startTimeInUTC)
        month == agendaMonth.number && year == currentYear
    }
}

fun List<AgendaEvent>.hasHiddenEvents(): Boolean {
    return this.any { it.isHidden.not() }
}

fun List<AgendaEvent>.getInRangeOf(
    agendaMonthYear: AgendaMonthYear
): List<AgendaEvent> {
    return this.filter { event -> agendaMonthYear.isEpochDayInRange(event.epochDay!!) }
}

fun List<AgendaEvent>.today(epochDay: Int): List<AgendaEvent>{
    return this.filter { it.epochDay == epochDay }
}

fun List<AgendaEvent>.getInRangeOf(agendaYear: AgendaYear): List<AgendaEvent>{
    return this.filter { event -> agendaYear.isEpochDayInRange(event.epochDay!!) }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

fun getMonthAndYearFromUnixTimestamp(unixTimestamp: Long): Pair<Int, Int> {
    var seconds = unixTimestamp
    var year = 1970

    // Calculate the year
    while (true) {
        val secondsInYear = if (isLeapYear(year)) 366 * 24 * 60 * 60 else 365 * 24 * 60 * 60
        if (seconds < secondsInYear) break
        seconds -= secondsInYear
        year += 1
    }

    // Days in each month, with February adjusted for leap years
    val daysInMonths = if (isLeapYear(year)) {
        arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    } else {
        arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }

    // Calculate the month
    var month = 1
    for (daysInMonth in daysInMonths) {
        val secondsInMonth = daysInMonth * 24 * 60 * 60
        if (seconds < secondsInMonth) break
        seconds -= secondsInMonth
        month += 1
    }

    return Pair(month, year)
}