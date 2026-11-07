package domain.model

import kotlinx.datetime.DayOfWeek

enum class DayInWeek(val position: Int, val res: String) {
    Monday(1, "Monday"),
    Tuesday(2, "Tuesday"),
    Wednesday (3, "Wednesday"),
    Thursday (4, "Thursday"),
    Friday (5, "Friday"),
    Saturday (6, "Saturday"),
    Sunday (7, "Sunday");

    val short: String
        get() = res.substring(0,3)

    val firstLetter: String
        get() = res.first().toString()
}

fun DayOfWeek.toDaysInWeek(): DayInWeek {
    return when (this) {
        DayOfWeek.MONDAY -> DayInWeek.Monday
        DayOfWeek.TUESDAY -> DayInWeek.Tuesday
        DayOfWeek.WEDNESDAY -> DayInWeek.Wednesday
        DayOfWeek.THURSDAY -> DayInWeek.Thursday
        DayOfWeek.FRIDAY -> DayInWeek.Friday
        DayOfWeek.SATURDAY -> DayInWeek.Saturday
        DayOfWeek.SUNDAY -> DayInWeek.Sunday
    }
}