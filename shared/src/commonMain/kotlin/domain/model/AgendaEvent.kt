package domain.model

import data.local.HiddenEventEntity
import extensions.currentLocalDateTime
import extensions.takingScreenshots
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AgendaEvent(
    val id: String,
    val title: String,
    val location: String? = "",
    val startTimeInUTC: Long,
    val endTimeInUTC: Long? = null,
    val allDay: Boolean,
    var epochDay: Long?,
    var isRecurring: Boolean = false,
    var isHidden: Boolean,
    val startLocalDateTime: LocalDateTime,
    val endLocalDateTime: LocalDateTime,
    val uses24HourClock: Boolean,
    val isMultiDay: Boolean,
    val isLastDayInMultiDayEvent: Boolean = false,
    /** Display a timed event as covering an entire intermediate day without changing its calendar type. */
    val isIntermediateDay: Boolean = false,
) {
    val displaysAllDay: Boolean get() = allDay || isIntermediateDay
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
        val localDateTime = if (isLastDayInMultiDayEvent) {
            endLocalDateTime
        } else {
            startLocalDateTime
        }

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

    fun hasPassedForToday(today: Long): Boolean {
        if (takingScreenshots) return false
        if (epochDay != today) return false
        return endLocalDateTime < currentLocalDateTime
    }
}
