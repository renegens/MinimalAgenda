package extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Long.millisToEpochDay(): Long {
    val instant = Instant.fromEpochMilliseconds(this)

    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone).date

    return localDate.toEpochDays()
}

@OptIn(ExperimentalTime::class)
fun Long.millisToTime(): String {
    val instant = Instant.fromEpochSeconds(this)

    // Convert Instant to a LocalDateTime in the desired timezone
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Extract the hour and minute
    val hours = dateTime.hour
    val minutes = dateTime.minute

    // Manually format hours and minutes to always have two digits
    val hoursString = if (hours < 10) "0$hours" else "$hours"
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"

    return "$hoursString:$minutesString"
}

@OptIn(ExperimentalTime::class)
fun Long.millisToHoursAndMinutes(): Pair<String, String> {
    val instant = Instant.fromEpochSeconds(this)

    // Convert Instant to a LocalDateTime in the desired timezone
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Extract the hour and minute
    val hours = dateTime.hour
    val minutes = dateTime.minute

    // Manually format hours and minutes to always have two digits
    val hoursString = if (hours < 10) "0$hours" else "$hours"
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"

    return hoursString to minutesString
}