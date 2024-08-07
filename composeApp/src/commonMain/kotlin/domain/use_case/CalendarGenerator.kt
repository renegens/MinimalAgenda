package domain.use_case

import data.toAgendaMonth
import domain.model.AgendaDay
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalendarGenerator {

    suspend operator fun invoke(): List<AgendaDay> {
        return withContext(Dispatchers.IO) {
            return@withContext generateDays().toDayData()
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

    private fun List<LocalDate>.toDayData(): List<AgendaDay> {
        val list = mutableListOf<AgendaDay>()
        this.forEach {
            val day = AgendaDay(
                agendaMonthYear = AgendaMonthYear(
                    agendaYear = AgendaYear(it.year),
                    agendaMonth = it.month.toAgendaMonth()
                ),
                dayOfWeek = it.dayOfWeek,
                dayOfMonth = it.dayOfMonth,
                epochDay = it.toEpochDays(),
                events = emptyList()
            )
            list.add(day)
        }

        return list
    }
}