package data

import extensions.toDigits
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

data class DayData(
    val month: Month,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val year: Int,
    val epochDay: Int
)