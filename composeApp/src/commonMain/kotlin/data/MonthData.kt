package data

import extensions.PRIME_NUMBER
import kotlinx.datetime.Month
import kotlinx.datetime.number

data class MonthData(val month: Month, val days: List<Int>, val year: Int) {

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
       return this.id == (other as MonthData).id
    }

    val id = year * PRIME_NUMBER + month.number
}

enum class Months(val nameRes: String) {
    January("Jan"),
    February("Feb"),
    March("March"),
    April("Apr"),
    May("May"),
    June("Jun"),
    July("Jul"),
    August("Aug"),
    September("Sep"),
    October("Oct"),
    November("Nov"),
    December("Dec")
}