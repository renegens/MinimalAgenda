package domain.model

import data.local.HiddenEventEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
    val formattedTime: String
) {
    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)

    fun idWithoutRID() = id.substringBeforeLast("/RID=")

    fun getStartTimeForPicker(): Long {
        return if (allDay){
            startTimeInUTC * 1000 + 6000

        }else{
            startTimeInUTC * 1000

        }
    }

    fun getHoursAndMinutes(): Pair<Int, Int> {
        val date = Instant.fromEpochSeconds(startTimeInUTC).toLocalDateTime(TimeZone.currentSystemDefault())
        return date.hour to date.minute
    }

    fun getDuration(): Int {
        return (endTime?.minus(startTimeInUTC))?.div(60)?.toInt() ?: 60
    }

    fun hasPassedForToday(today: Int): Boolean {
        if (epochDay != today) return false
        val eventDate = Instant.fromEpochSeconds(endTime ?: 0).toLocalDateTime(TimeZone.currentSystemDefault())
        val currentLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return eventDate < currentLocalDateTime
    }
}