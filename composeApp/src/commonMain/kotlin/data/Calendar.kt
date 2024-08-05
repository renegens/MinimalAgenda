package data

import kotlinx.datetime.LocalDate

data class Calendar(
    val days: List<DayData>,
    val months: List<MonthData>,
    val years: List<YearData>,
    val monthIndex: Int,
    val dayIndex: Int,
    val yearIndex: Int,
    val today: LocalDate
)