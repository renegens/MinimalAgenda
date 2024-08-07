package domain.model

import extensions.toDigits
import extensions.today

data class AgendaYear(
    val year: Int
){
    val firstPart: String
        get() = year.toDigits().take(2).joinToString(separator = "")

    val secondPart: String
        get() = year.toDigits().takeLast(2).joinToString(separator = "")

    val isCurrent: Boolean
        get() = today.year == year
}