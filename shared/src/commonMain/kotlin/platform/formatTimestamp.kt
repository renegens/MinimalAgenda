package platform

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** Date pickers represent a calendar date as midnight UTC, in milliseconds. */
fun formatTimestamp(timestamp: Long): String =
    formatLocalDate(Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.UTC).date)

expect fun formatLocalDate(date: LocalDate): String
expect fun formatLocalTime(hour: Int, minute: Int): String
expect fun formatDayAndMonth(day: Int, month: Int): String
expect fun formatMonthName(month: Int, abbreviated: Boolean = false): String
