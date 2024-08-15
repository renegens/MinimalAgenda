package domain.model

import data.local.HiddenEventEntity

data class AgendaEvent(
    val id: String,
    val title: String,
    val startTime: Long,
    var epochDay: Int?,
    var isHidden: Boolean
) {
    fun toHiddenEventEntity() = HiddenEventEntity(eventId = id, epochDay = epochDay!!)
}