package use_case

import extensions.getStartDayOfMonth
import extensions.toDays
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


fun getDaysInMonthWithZeroes(year: Int, month: Month): List<Int> {
    //val daysInMonth = daysInMonth(year, month.number)
    val daysInMonth = month.toDays(year)
    //val startDayOfWeek = getStartOfMonth(year, month).dayOfWeek.isoDayNumber
    val startDayOfWeek = month.getStartDayOfMonth(year)

    val zerosToPrepend = 7 - startDayOfWeek
    val days = mutableListOf<Int>()

    for (i in 1..zerosToPrepend) {
        days.add(0)
    }

    for (day in 1..daysInMonth) {
        days.add(day)
    }

    val totalRows = if (zerosToPrepend < 5) {
        35
    } else {
        42
    }

    val zeroToAppend = totalRows - days.size

    for (i in 1..zeroToAppend) {
        days.add(0)
    }
    return days
}


fun isToday(date: LocalDate): Int? {
    // Get the current date
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    // Compare the given date with the current date
    return if (date == currentDate) {
        currentDate.dayOfMonth
    } else {
        null
    }
}

fun getStartOfMonth(year: Int, month: Month): LocalDate {
    // Create a new LocalDate for the first day of the same month
    return LocalDate(year, month.number, 1)
}

fun getStartOfWeek(date: LocalDate): LocalDate {
    // Get the day of the week as an integer (Monday is 1, Sunday is 7)
    val dayOfWeek = date.dayOfWeek.isoDayNumber
    // Calculate how many days to subtract to get back to Monday
    val daysToSubtract = dayOfWeek - 1
    // Subtract the days to get the start of the week
    return date.minus(DatePeriod(days = daysToSubtract))
}

fun daysInMonth(year: Int, month: Int): Int {
    // Create a LocalDate for the first day of the given month and year
    val firstDayOfMonth = LocalDate(year, month, 1)

    // Create a second LocalDate by adding one month to the first day
    val firstDayOfNextMonth = firstDayOfMonth.plus(DatePeriod(months = 1))

    // Calculate the difference in days between the two dates
    val daysInMonth = firstDayOfNextMonth.toEpochDays() - firstDayOfMonth.toEpochDays()

    return daysInMonth.toInt()
}

fun generateDayEntries(startDate: LocalDate): List<LocalDate> {
    // Calculate the start and end dates
    val startDate = startDate.minus(DatePeriod(years = 2))
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


fun getMonths(): List<LocalDate> {
    // Get the current date
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Calculate the start and end dates
    val startDate = currentDate.minus(DatePeriod(years = 2))
    val endDate = currentDate.plus(DatePeriod(years = 2))

    // Initialize the list to hold LocalDate objects representing the first day of each month
    val dateList = mutableListOf<LocalDate>()

    // Start from the first day of the start month
    var currentMonth = LocalDate(startDate.year, startDate.month, 1)

    // Populate the list with the first day of each month in the range
    while (currentMonth <= LocalDate(endDate.year, endDate.month, 1)) {
        dateList.add(currentMonth)
        currentMonth = currentMonth.plus(DatePeriod(months = 1))
    }

    // Print the list
    return dateList
    //dateList.forEach { println(it) }
}
