package domain.model

import kotlinx.datetime.Month
import kotlinx.datetime.number

enum class AgendaMonth(val number: Int, val nameRes: String) {
    January(1, "Jan"),
    February(2, "Feb"),
    March(3, "March"),
    April(4, "Apr"),
    May(5, "May"),
    June(6, "Jun"),
    July(7, "Jul"),
    August(8, "Aug"),
    September(9, "Sep"),
    October(10, "Oct"),
    November(11, "Nov"),
    December(12, "Dec"),
    Unexpected(-1, "")
}

fun AgendaMonth.toMonth() = when (this.number) {
    1 -> Month.JANUARY
    2 -> Month.FEBRUARY
    3 -> Month.MARCH
    4 -> Month.APRIL
    5 -> Month.MAY
    6 -> Month.JUNE
    7 -> Month.JULY
    8 -> Month.AUGUST
    9 -> Month.SEPTEMBER
    10 -> Month.OCTOBER
    11 -> Month.NOVEMBER
    12 -> Month.DECEMBER

    else -> Month.DECEMBER
}

fun Month.toAgendaMonth(): AgendaMonth {
    return when (this.number) {
        1 -> AgendaMonth.January
        2 -> AgendaMonth.February
        3 -> AgendaMonth.March
        4 -> AgendaMonth.April
        5 -> AgendaMonth.May
        6 -> AgendaMonth.June
        7 -> AgendaMonth.July
        8 -> AgendaMonth.August
        9 -> AgendaMonth.September
        10 -> AgendaMonth.October
        11 -> AgendaMonth.November
        12 -> AgendaMonth.December
        else -> AgendaMonth.Unexpected
    }
}