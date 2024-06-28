package data

import extensions.PRIME_NUMBER
import extensions.toDigits
import kotlinx.datetime.Month
import kotlinx.datetime.number

data class MonthData(val month: Month, val days: List<Int>, val year: Int) {

    override fun hashCode(): Int {
        return id
    }

    val id = year * PRIME_NUMBER + month.number
}