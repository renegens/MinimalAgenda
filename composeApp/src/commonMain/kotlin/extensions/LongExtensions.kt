package extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

fun Long.millisToEpochDay(): Int {
    val instant = Instant.fromEpochMilliseconds(this)

    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone).date

    return localDate.toEpochDays()
}

fun Long.millisToTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)

    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone)

    return localDate.time.toString()
}