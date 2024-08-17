package domain.model

import extensions.toDigits
import extensions.today
import kotlinx.datetime.LocalDate

data class AgendaYear(
    val year: Int
){
    val firstTwoDigits: String
        get() = year.toDigits().take(2).joinToString(separator = "")

    val lastTwoDigits: String
        get() = year.toDigits().takeLast(2).joinToString(separator = "")

    val isCurrent: Boolean
        get() = today.year == year

    fun isEpochDayInRange(epochDay: Int): Boolean {
        val range = epochRange
        return IntRange(range.first, range.second).contains(epochDay)
    }

    private val epochRange: Pair<Int, Int>
        get() {
            val firstDayOfYear = LocalDate(year, 1, 1)
            val firstEpochDay = firstDayOfYear.toEpochDays()

            val lastDayOfYear = LocalDate(year, 12, 31)
            val lastEpochDay = lastDayOfYear.toEpochDays()

            return Pair(firstEpochDay, lastEpochDay)
        }
}