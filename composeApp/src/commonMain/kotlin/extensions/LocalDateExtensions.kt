package extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val takingScreenshots = true
private val epochDay4September2024 = 1725408000L

val today: LocalDate
    get() {
        return if (takingScreenshots) {
            Instant.fromEpochSeconds(epochDay4September2024).toLocalDateTime(TimeZone.currentSystemDefault()).date
        }else{
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
    }

val nowInMillis: Long
    get() = Clock.System.now().toEpochMilliseconds()

val nextHour: Int
    get() {
        val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val newHour = date.time.hour + 1
        return if (newHour > 24) {
            24
        } else {
            newHour
        }
    }