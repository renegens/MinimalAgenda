package extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

val PRIME_NUMBER = 31

fun LocalDate.uniqueMonthID() = year * PRIME_NUMBER + month.number

val today =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

val todayEpochDay = today.toEpochDays()