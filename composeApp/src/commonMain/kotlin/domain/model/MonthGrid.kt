package domain.model

import domain.model.MonthGrid.Companion.LARGE_ROW
import domain.model.MonthGrid.Companion.MAX_ZEROES_TO_PREPEND
import domain.model.MonthGrid.Companion.SMALL_ROW
import extensions.getStartDayOfMonth
import extensions.toDaysInMonth
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class MonthGrid {

    companion object {
        private const val SMALL_ROW = 35
        private const val LARGE_ROW = 42
        private const val MAX_ZEROES_TO_PREPEND = 5
    }

    fun fillZeros(year: Int, month: Month): List<Int> {
        val daysInMonth = month.toDaysInMonth(year)
        val startDayOfWeek = month.getStartDayOfMonth(year)

        val zerosToPrepend = startDayOfWeek - 1
        val days = mutableListOf<Int>()

        for (i in 1..zerosToPrepend) {
            days.add(0)
        }

        for (day in 1..daysInMonth) {
            days.add(day)
        }

        val totalRows = if (zerosToPrepend < MAX_ZEROES_TO_PREPEND) {
            SMALL_ROW
        } else {
            LARGE_ROW
        }

        val zeroToAppend = totalRows - days.size

        for (i in 1..zeroToAppend) {
            days.add(0)
        }
        return days
    }

    fun fillZeros(localDate: LocalDate): List<Int> {
        val daysInMonth = localDate.month.toDaysInMonth(localDate.year)
        val startDayOfWeek = localDate.month.getStartDayOfMonth(localDate.year)

        val zerosToPrepend = startDayOfWeek - 1
        val days = mutableListOf<Int>()

        for (i in 1..zerosToPrepend) {
            days.add(0)
        }

        for (day in 1..daysInMonth) {
            days.add(day)
        }

        val totalRows = if (zerosToPrepend < MAX_ZEROES_TO_PREPEND) {
            SMALL_ROW
        } else {
            LARGE_ROW
        }

        val zeroToAppend = totalRows - days.size

        for (i in 1..zeroToAppend) {
            days.add(0)
        }
        return days
    }


}

private const val SMALL_ROW = 35
private const val LARGE_ROW = 42
private const val MAX_ZEROES_TO_PREPEND = 5

fun createDaysWithZeros(year: Int, month: Month): List<Pair<Int, LocalDate?>> {
    val daysInMonth = month.toDaysInMonth(year)
    val startDayOfWeek = month.getStartDayOfMonth(year)

    val zerosToPrepend = startDayOfWeek - 1
    val days = mutableListOf<Pair<Int,LocalDate?>>()

    for (i in 1..zerosToPrepend) {
        days.add(Pair(0, null))
    }

    for (day in 1..daysInMonth) {
        val local = LocalDate(year = year, month = month, dayOfMonth = day)
        days.add(Pair(day, local))
    }

    val totalRows = if (zerosToPrepend < MAX_ZEROES_TO_PREPEND) {
        SMALL_ROW
    } else {
        LARGE_ROW
    }

    val zeroToAppend = totalRows - days.size

    for (i in 1..zeroToAppend) {
        days.add(Pair(0, null))
    }
    return days
}

fun createGridDay(year: Int, month: Month): List<GridDay> {
    val daysInMonth = month.toDaysInMonth(year)
    val startDayOfWeek = month.getStartDayOfMonth(year)

    val zerosToPrepend = startDayOfWeek - 1
    val days = mutableListOf<GridDay>()

    for (i in 1..zerosToPrepend) {
        days.add(GridDay())
    }

    for (day in 1..daysInMonth) {
        val local = LocalDate(year = year, month = month, dayOfMonth = day)
        days.add(GridDay(day, local.toEpochDays()))
    }

    val totalRows = if (zerosToPrepend < MAX_ZEROES_TO_PREPEND) {
        SMALL_ROW
    } else {
        LARGE_ROW
    }

    val zeroToAppend = totalRows - days.size

    for (i in 1..zeroToAppend) {
        days.add(GridDay())
    }
    return days
}

class GridDay(val dayInMonth: Int = DONT_RENDER, val epochDay: Int = DONT_RENDER) {
    companion object {
        const val DONT_RENDER = -1
    }
}