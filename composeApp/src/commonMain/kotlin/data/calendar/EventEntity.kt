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
    var epochDay: Int,
    val startLocalDateTime: LocalDateTime,
    val endLocalDateTime: LocalDateTime,
    val isMultiDay: Boolean,
    val uses24HourClock: Boolean
)

fun EventEntity.toEvent(): AgendaEvent {

    //todo add default event with values

    return AgendaEvent(
        id = id!!,
        title = title!!,
        location = location,
        startTimeInUTC = startTime!!,
        endTimeInUTC = endTime,
        allDay = allDay!!,
        isRecurring = isRecurring!!,
        isMultiDay = isMultiDay,
        epochDay = epochDay,
        isHidden = false,
        startLocalDateTime = startLocalDateTime,
        endLocalDateTime = endLocalDateTime,
        uses24HourClock = uses24HourClock
    )
}