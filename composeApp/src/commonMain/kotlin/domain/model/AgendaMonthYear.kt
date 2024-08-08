package domain.model

import data.AgendaMonth
import extensions.today
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus

data class AgendaMonthYear(val agendaYear: AgendaYear, val agendaMonth: AgendaMonth) {

    val daysInMonth: Int
        get() {
            val firstDayOfMonth = LocalDate(agendaYear.year, agendaMonth.number, 1)
            val firstDayOfNextMonth = firstDayOfMonth.plus(DatePeriod(months = 1))
            val daysInMonth = firstDayOfNextMonth.toEpochDays() - firstDayOfMonth.toEpochDays()

            return daysInMonth
        }

    val startDayOfMonth: Int
        get() = LocalDate(agendaYear.year, agendaMonth.number, 1).dayOfWeek.isoDayNumber

    val year: Int
        get() = agendaYear.year

    val isCurrent: Boolean
        get() {
            val current = today
            return agendaMonth.number == current.month.number && year == current.year
        }

    fun isSame(agendaMonthYear: AgendaMonthYear): Boolean {
        return agendaMonthYear.agendaYear.year == agendaYear.year && agendaMonthYear.agendaMonth.number == agendaMonth.number
    }
}