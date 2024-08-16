package domain.model

import data.local.HiddenEventEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AgendaEvent(
    val id: String,
    val title: String,
    val startTime: Long,
    val allDay: Boolean,
    var epochDay: Int?,
    var isHidden: Boolean
) {

    val formatedStartTime: Pair<String, String>
        get() {
            val instant = Instant.fromEpochSeconds(startTime)

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

    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)
}