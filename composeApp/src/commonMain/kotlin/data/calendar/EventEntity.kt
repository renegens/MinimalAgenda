package data.calendar

import domain.model.AgendaEvent

data class EventEntity(
    val id: String?,
    val title: String?,
    val location: String?,
    val startTime: Long?,
    val endTime: Long?,
    val allDay: Boolean?,
    val isRecurring: Boolean?,
    var epochDay: Int? = null,
    val format: String = ""
)

fun EventEntity.toEvent(): AgendaEvent {

    //todo add default event with values

    return AgendaEvent(
        id = id!!,
        title = title!!,
        location = location,
        startTime = startTime!!,
        endTime = endTime,
        allDay = allDay!!,
        isRecurring = isRecurring!!,
        epochDay = epochDay,
        isHidden = false,
        formattedTime = format
    )
}