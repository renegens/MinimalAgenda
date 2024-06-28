package data

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime


class DateGenerator {

    private val currentDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    fun generateDays(): List<LocalDate> {
        // Calculate the start and end dates
        val startDate = currentDate.minus(DatePeriod(years = 2))
        val endDate = startDate.plus(DatePeriod(years = 4)) // Total range is 4 years

        // Initialize the list to hold LocalDate objects
        val dateList = mutableListOf<LocalDate>()

        // Populate the list with each day in the range
        var currentDate = startDate
        while (currentDate <= endDate) {
            dateList.add(currentDate)
            currentDate = currentDate.plus(DatePeriod(days = 1))
        }

        return dateList
    }
}

fun List<LocalDate>.byYears(): Map<Int, List<LocalDate>> {
    return this.groupBy { it.year }
}

fun List<LocalDate>.toMonthData(): List<MonthData> {
    val monthGridViewData = MonthGridViewData()
    val list = mutableSetOf<MonthData>()
    this.forEach {
        val monthGrid = monthGridViewData.fillZeros(year = it.year, month = it.month)
        val month = MonthData(it.month, monthGrid, it.year)
        list.add(month)
    }

    return list.toList()
}

fun List<LocalDate>.toDayData(): List<DayData> {
    val list = mutableListOf<DayData>()

    this.forEach {
        val day = DayData(
            month = it.month,
            dayOfWeek = it.dayOfWeek,
            dayOfMonth = it.dayOfMonth,
            year = it.year,
            epochDay = it.toEpochDays()
        )
        list.add(day)
    }

    return list
}



