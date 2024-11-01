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
    val localDateTime: LocalDateTime,
    val uses24HourClock: Boolean
)

fun EventEntity.toEvent(): AgendaEvent {

    //todo add default event with values

    return AgendaEvent(
        id = id!!,
        title = title!!,
        location = location,
        startTimeInUTC = startTime!!,
        endTime = endTime,
        allDay = allDay!!,
        isRecurring = isRecurring!!,
        epochDay = epochDay,
        isHidden = false,
        localDateTime = localDateTime,
        uses24HourClock = uses24HourClock
    )
}