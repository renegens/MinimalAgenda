package domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class EventTime(
    private val startTimestampInMillis: Long,
    private val startHours: Int,
    private val startMinutes: Int,
    private val durationInMinutes: Int
) {

    fun getStarDateTime(): LocalDateTime {
        return getLocalDateTime(
            timestamp = startTimestampInMillis,
            hours = startHours,
            minutes = startMinutes
        )
    }

    fun getEndDateTime(): LocalDateTime {
        val quotient = durationInMinutes / 60
        val reminder = durationInMinutes % 60
        return getLocalDateTime(
            timestamp = startTimestampInMillis,
            hours = startHours + quotient,
            minutes = reminder
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun getLocalDateTime(timestamp: Long, hours: Int, minutes: Int): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val localDate = instant.date
        val localTime = LocalTime(hour = hours, minute = minutes)

        return LocalDateTime(date = localDate, time = localTime)
    }

}