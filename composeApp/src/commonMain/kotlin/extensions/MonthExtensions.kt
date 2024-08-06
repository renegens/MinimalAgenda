package extensions

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus

fun Month.toDaysInMonth(year: Int): Int {
    // Create a LocalDate for the first day of the given month and year
    val firstDayOfMonth = LocalDate(year, this, 1)

    // Create a second LocalDate by adding one month to the first day
    val firstDayOfNextMonth = firstDayOfMonth.plus(DatePeriod(months = 1))

    // Calculate the difference in days between the two dates
    val daysInMonth = firstDayOfNextMonth.toEpochDays() - firstDayOfMonth.toEpochDays()

    return daysInMonth
}

fun Month.getStartDayOfMonth(year: Int): Int {
    return LocalDate(year, this.number, 1).dayOfWeek.isoDayNumber
}