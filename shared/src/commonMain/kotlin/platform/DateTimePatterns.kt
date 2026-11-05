package platform

/** Pattern letters inside quoted literals are text, including escaped apostrophes. */
internal fun unquotedPatternLetters(pattern: String): String = buildString {
    var quoted = false
    var index = 0
    while (index < pattern.length) {
        val char = pattern[index]
        if (char == '\'') {
            if (pattern.getOrNull(index + 1) == '\'') {
                index++
            } else {
                quoted = !quoted
            }
        } else if (!quoted && char.isLetter()) {
            append(char)
        }
        index++
    }
}

internal fun compactDayAndMonth(day: Int, month: Int, localizedPattern: String): String {
    val fields = unquotedPatternLetters(localizedPattern)
    val firstField = fields.firstOrNull { it == 'd' || it == 'M' || it == 'L' }
    return if (firstField == 'M' || firstField == 'L') "$month.$day" else "$day.$month"
}

internal fun patternUses24HourClock(pattern: String): Boolean =
    unquotedPatternLetters(pattern).any { it == 'H' || it == 'k' }
