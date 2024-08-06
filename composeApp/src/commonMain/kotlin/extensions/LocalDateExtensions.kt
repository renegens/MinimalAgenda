package extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

val PRIME_NUMBER = 31

fun LocalDate.uniqueMonthID() = year * PRIME_NUMBER + month.number