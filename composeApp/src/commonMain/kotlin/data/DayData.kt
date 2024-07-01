package data

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month

data class DayData(
    val month: Month,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val year: Int,
    val epochDay: Int
)