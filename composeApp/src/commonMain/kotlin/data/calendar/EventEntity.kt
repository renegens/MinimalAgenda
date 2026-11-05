package data.calendar

import domain.model.AgendaEvent
import kotlinx.datetime.LocalDateTime

data class EventEntity(
    val id: String?,
    val title: String?,
    val location: String?,
    val startTime: Long?,
    val endTime: Long?,
    val allDay: Boolean?,
    val isRecurring: Boolean?,
    var epochDay: Long,
    val startLocalDateTime: LocalDateTime,
    val endLocalDateTime: LocalDateTime,
    val isMultiDay: Boolean,
    val uses24HourClock: Boolean
)

/**
 * Maps the entity to an [AgendaEvent], or returns null when a required field (id or start time) is
 * missing so that one bad event can be skipped without failing the whole load.
 * Optional fields fall back to defaults: title -> "", allDay/isRecurring -> false.
 */
fun EventEntity.toEvent(): AgendaEvent? {
    val id = id ?: return null
    val startTime = startTime ?: return null

    return AgendaEvent(
        id = id,
        title = title.orEmpty(),
        location = location,
        startTimeInUTC = startTime,
        endTimeInUTC = endTime,
        allDay = allDay ?: false,
        isRecurring = isRecurring ?: false,
        isMultiDay = isMultiDay,
        epochDay = epochDay,
        isHidden = false,
        startLocalDateTime = startLocalDateTime,
        endLocalDateTime = endLocalDateTime,
        uses24HourClock = uses24HourClock
    )
}