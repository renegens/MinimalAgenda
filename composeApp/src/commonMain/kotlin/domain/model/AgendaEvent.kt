package domain.model

import data.local.HiddenEventEntity

data class AgendaEvent(
    val id: String,
    val title: String,
    val startTime: Long,
    val allDay: Boolean,
    var epochDay: Int?,
    var isHidden: Boolean,
    val formattedTime: String
) {
    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)
}