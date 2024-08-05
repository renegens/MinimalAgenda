package data

import extensions.uniqueMonthID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class Calendar(
    val days: List<DayData>,
    val months: List<MonthData>,
    val years: List<YearData>,
    val monthIndex: Int,
    val dayIndex: Int,
    val yearIndex: Int,
    val today: LocalDate
)


class CalendarGenerator {

    suspend fun run(): Calendar {
        return withContext(Dispatchers.IO) {
            val allDays = generateDays()
            val days = allDays.toDayData()
            val months = allDays.toMonthData()
            val years = allDays.toYearData()

            val monthIndexToScrollTo = months.indexOfFirst { it.id == currentDate.uniqueMonthID() }
            val dayIndexToScrollTo = days.last().epochDay - currentDate.toEpochDays()
            val yearIndexToScrollTo = years.indexOfFirst { it.year == currentDate.year }

            Calendar(
                days = days,
                months = months,
                years = years,
                monthIndex = monthIndexToScrollTo,
                yearIndex = yearIndexToScrollTo,
                dayIndex = dayIndexToScrollTo,
                today = currentDate
            )
        }
    }


    private val currentDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun generateDays(): List<LocalDate> {
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


    private fun List<LocalDate>.toMonthData(): List<MonthData> {
        val monthGridViewData = MonthGridViewData()
        val list = mutableSetOf<MonthData>()
        this.forEach {
            val monthGrid = monthGridViewData.fillZeros(year = it.year, month = it.month)
            val month = MonthData(it.month, monthGrid, it.year)
            list.add(month)
        }

        return list.toList()
    }

    private fun List<LocalDate>.toDayData(): List<DayData> {
        val list = mutableListOf<DayData>()

        this.forEach {
            val day = DayData(
                month = it.month,
                dayOfWeek = it.dayOfWeek,
                dayOfMonth = it.dayOfMonth,
                year = it.year,
                epochDay = it.toEpochDays(),
                events = emptyList()
            )
            list.add(day)
        }

        return list
    }

    private fun List<LocalDate>.toYearData(): List<YearData> {
        val list = mutableListOf<YearData>()

        this.groupBy { it.year }.entries.forEach {
            val year = YearData(year = it.key)
            list.add(year)
        }

        return list
    }
}




