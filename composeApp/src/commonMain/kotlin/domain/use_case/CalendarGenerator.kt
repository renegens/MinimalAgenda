package domain.use_case

import domain.model.Agenda
import domain.model.AgendaDay
import domain.model.AgendaMonth
import domain.model.AgendaYear
import domain.model.MonthGrid
import extensions.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalendarGenerator {

    suspend fun run(): Agenda {
        return withContext(Dispatchers.IO) {
            val allDays = generateDays()
            val days = allDays.toDayData()
            val years = allDays.toYearData()

            // TODO: They need to be calculated on the view level at refresh since the date may have changed

            val yearIndexToScrollTo = years.indexOfFirst { it.year == today.year }

            Agenda(
                days = days,
                years = years,
                yearIndex = yearIndexToScrollTo,
            )
        }
    }

    private fun generateDays(): List<LocalDate> {
        // Calculate the start and end dates
        val startDate = today.minus(DatePeriod(years = 2))
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