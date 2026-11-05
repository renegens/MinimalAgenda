package domain.use_case

import domain.model.AgendaDay
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import domain.model.toAgendaMonth
import domain.model.toDaysInWeek
import extensions.today
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class CalendarGenerator {

    val calendar: List<AgendaDay> = generateDays().toDayData()

    private fun generateDays(): List<LocalDate> {

        val startYear = LocalDate(year = today.year - 2, month = 1, day = 1)
        val endDate = LocalDate(year = startYear.year + 4, month = 12, day = 31)

        // Initialize the list to hold LocalDate objects
        val dateList = mutableListOf<LocalDate>()

        // Populate the list with each day in the range
        var currentDate = startYear
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
                dayInWeek = it.dayOfWeek.toDaysInWeek(),
                dayOfMonth = it.day,
                epochDay = it.toEpochDays(),
            )
            list.add(day)
        }

        return list
    }
}