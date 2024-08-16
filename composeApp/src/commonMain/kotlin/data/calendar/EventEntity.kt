package data.calendar

import domain.model.AgendaEvent

data class EventEntity(
    val id: String?,
    val title: String?,
    val startTime: Long?,
    val allDay: Boolean?,
    var epochDay: Int? = null
)

fun EventEntity.toEvent(): AgendaEvent {

    //todo add default event with values

    return AgendaEvent(
        id = id!!,
        title = title!!,
        startTime = startTime!!,
        allDay = allDay!!,
        epochDay = epochDay,
        isHidden = false
    )
}