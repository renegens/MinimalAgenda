package domain.use_case

import domain.model.Agenda
import domain.model.AgendaDay
import domain.model.AgendaMonth
import domain.model.AgendaYear
import domain.model.MonthGrid
import extensions.uniqueMonthID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class CalendarGenerator {

    suspend fun run(): Agenda {
        return withContext(Dispatchers.IO) {
            val allDays = generateDays()
            val days = allDays.toDayData()

            val yearsFromDays = days.groupBy { it.year }
            val monthsPerYear = yearsFromDays[2024]!!.groupBy { it.month }
            println(monthsPerYear)
            val months = allDays.toMonthData()
            val years = allDays.toYearData()


            // TODO: They need to be calculated on the view level at refresh since the date may have changed

            val monthIndexToScrollTo = months.indexOfFirst { it.id == currentDate.uniqueMonthID() }
            val dayIndexToScrollTo = days.last().epochDay - currentDate.toEpochDays()
            val yearIndexToScrollTo = years.indexOfFirst { it.year == currentDate.year }

            Agenda(
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

    private fun List<LocalDate>.toMonthData(): List<AgendaMonth> {
        val monthGrid = MonthGrid()
        val list = mutableSetOf<AgendaMonth>()
        this.forEach { localDate ->

            val monthGridView = monthGrid.fillZeros(localDate)
            val month = AgendaMonth(
                month = localDate.month,
                days = monthGridView,
                year = localDate.year
            ).apply {
                epochDays += localDate.toEpochDays()
            }
            list.add(month)
        }

        return list.toList()
    }

    private fun List<LocalDate>.toDayData(): List<AgendaDay> {
        val list = mutableListOf<AgendaDay>()
        this.forEach {
            val day = AgendaDay(
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

    private fun List<LocalDate>.toYearData(): List<AgendaYear> {
        val list = mutableListOf<AgendaYear>()

        this.groupBy { it.year }.entries.forEach {
            val year = AgendaYear(year = it.key)
            list.add(year)
        }

        return list
    }

}