package domain.model

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus

data class AgendaMonthYear(val agendaYear: AgendaYear, val agendaMonth: AgendaMonth) {

    val daysInMonth: Long
        get() {
            val firstDayOfMonth = LocalDate(agendaYear.year, agendaMonth.number, 1)
            val firstDayOfNextMonth = firstDayOfMonth.plus(DatePeriod(months = 1))
            val daysInMonth = firstDayOfNextMonth.toEpochDays() - firstDayOfMonth.toEpochDays()

            return daysInMonth
        }

    private val epochRange: Pair<Long, Long>
        get() {
            val firstDayOfMonth = LocalDate(agendaYear.year, agendaMonth.number, 1)
            val firstEpochDay = firstDayOfMonth.toEpochDays()

            val lastDayOfMonth = firstDayOfMonth
                .plus(DatePeriod(months = 1))  // Move to the first day of the next month
                .minus(DatePeriod(days = 1))   // Subtract one day to get the last day of the current month

            val lastEpochDay = lastDayOfMonth.toEpochDays()

            return Pair(firstEpochDay, lastEpochDay)
        }

    fun isEpochDayInRange(epochDay: Long): Boolean {
        val range = epochRange
        return LongRange(range.first, range.second).contains(epochDay)
    }

    val startDayOfMonth: Int
        get() = LocalDate(agendaYear.year, agendaMonth.number, 1).dayOfWeek.isoDayNumber

    val year: Int
        get() = agendaYear.year

    fun isCurrent(today: LocalDate): Boolean {
        return agendaMonth.number == today.month.number && year == today.year
    }

    fun isSame(agendaMonthYear: AgendaMonthYear): Boolean {
        return agendaMonthYear.agendaYear.year == agendaYear.year && agendaMonthYear.agendaMonth.number == agendaMonth.number
    }
}