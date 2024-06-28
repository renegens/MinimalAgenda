package data

import extensions.getStartDayOfMonth
import extensions.toDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class MonthGridViewData {

    companion object {
        private const val DAYS_IN_A_WEEK = 7
        private const val SMALL_ROW = 35
        private const val LARGE_ROW = 42
        private const val MAX_ZEROES_TO_PREPEND = 5
    }

    fun fillZeros(year: Int, month: Month): List<Int> {
        val daysInMonth = month.toDays(year)
        val startDayOfWeek = month.getStartDayOfMonth(year)

        val zerosToPrepend = DAYS_IN_A_WEEK - startDayOfWeek
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