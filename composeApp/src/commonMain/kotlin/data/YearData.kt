package data

import extensions.toDigits

data class YearData(
    val year: Int
){
    val firstPart: String
        get() = year.toDigits().take(2).joinToString(separator = "")

    val secondPart: String
        get() = year.toDigits().takeLast(2).joinToString(separator = "")
}