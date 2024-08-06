package domain.model

import extensions.PRIME_NUMBER
import extensions.toDigits
import extensions.toShortName
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

data class AgendaMonth(
    val month: Month,
    val days: List<Int>,
    val year: Int,
    var epochDays: List<Int> = emptyList()
) {

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        return this.id == (other as AgendaMonth).id
    }

    val id = year * PRIME_NUMBER + month.number

    val shortMonth = month.name.toShortName()

    val shortYear = year.toDigits().takeLast(2).joinToString(separator = "")
}

