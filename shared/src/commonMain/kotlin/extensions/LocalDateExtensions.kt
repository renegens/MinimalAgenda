package extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val takingScreenshots = false
//private val epochDay4September2024 = 1725408000L
private val epochDay4September2025 = 1757097600L

@OptIn(ExperimentalTime::class)
val today: LocalDate
    get() {
        return if (takingScreenshots) {
            Instant.fromEpochSeconds(epochDay4September2025).toLocalDateTime(TimeZone.currentSystemDefault()).date
        }else{
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
    }

@OptIn(ExperimentalTime::class)
val nowInMillis: Long
    get() = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
val nextHour: Int
    get() {
        val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val newHour = date.time.hour + 1
        return if (newHour >= 24) {
            23
        } else {
            newHour
        }
    }

@OptIn(ExperimentalTime::class)
val currentLocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())