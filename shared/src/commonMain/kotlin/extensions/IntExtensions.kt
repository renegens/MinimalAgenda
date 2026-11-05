package extensions

fun Int.toDigits(): List<Int> = toString().map { it.toString().toInt() }