package extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val today: LocalDate
    get() = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date

val nowInMillis: Long
    get() = Clock.System.now().toEpochMilliseconds()

val currentHour: Int
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour

val nextHour: Int
    get() {
        val newTime = nowInMillis + (60 * 60 * 1000)
        return Instant.fromEpochMilliseconds(newTime)
            .toLocalDateTime(TimeZone.currentSystemDefault()).hour
    }