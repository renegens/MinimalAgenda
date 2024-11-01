package domain.model

import data.local.HiddenEventEntity
import extensions.takingScreenshots
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AgendaEvent(
    val id: String,
    val title: String,
    val location: String? = "",
    val startTimeInUTC: Long,
    val endTime: Long? = null,
    val allDay: Boolean,
    var epochDay: Int?,
    var isRecurring: Boolean = false,
    var isHidden: Boolean,
    val localDateTime: LocalDateTime,
    val uses24HourClock: Boolean
) {
    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)

    fun idWithoutRID() = id.substringBeforeLast("/RID=")

    fun getStartTimeForPicker(): Long {
        return if (allDay) {
            startTimeInUTC * 1000 + 6000

        } else {
            startTimeInUTC * 1000

        }
    }

    fun getFormattedTime(): FormattedTime {
        val hour = localDateTime.time.hour
        val minute = localDateTime.time.minute

        if (uses24HourClock) {
            val hours = hour.toString().padStart(2, '0')
            val minutes = ":${minute.toString().padStart(2, '0')}"
            return FormattedTime(
                hours = hours,
                minutes = minutes
            )
        } else {
            val hour12 = if (hour % 12 == 0) 12 else hour % 12
            val amPm = if (hour < 12) "AM" else "PM"
            val minutes = ":${minute.toString().padStart(2, '0')}"
            return FormattedTime(
                hours = hour12.toString(),
                minutes = minutes,
                ampm = amPm
            )
        }
    }

    fun getHoursAndMinutes(): Pair<Int, Int> {
        val date = Instant.fromEpochSeconds(startTimeInUTC)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return date.hour to date.minute
    }

    fun getDuration(): Int {
        return (endTime?.minus(startTimeInUTC))?.div(60)?.toInt() ?: 60
    }

    fun hasPassedForToday(today: Int): Boolean {
        if (takingScreenshots) return false
        if (epochDay != today) return false
        val eventDate =
            Instant.fromEpochSeconds(endTime ?: 0).toLocalDateTime(TimeZone.currentSystemDefault())
        val currentLocalDateTime =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return eventDate < currentLocalDateTime
    }
}