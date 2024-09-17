package domain.model

import data.local.HiddenEventEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AgendaEvent(
    val id: String,
    val title: String,
    val location: String? = "",
    val startTime: Long,
    val endTime: Long? = null,
    val allDay: Boolean,
    var epochDay: Int?,
    var isRecurring: Boolean = false,
    var isHidden: Boolean,
    val formattedTime: String
) {
    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)

    fun getStartTimeForPicker(): Long {
        return if (allDay){
            startTime * 1000 + 6000

        }else{
            startTime * 1000

        }
    }

    fun getHoursAndMinutes(): Pair<Int, Int> {
        val date = Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.currentSystemDefault())
        return date.hour to date.minute
    }

    fun getDuration(): Int {
        return (endTime?.minus(startTime))?.div(60)?.toInt() ?: 60
    }
}