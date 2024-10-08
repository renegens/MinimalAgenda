package data.local

import kotlinx.serialization.Serializable

@Serializable
data class HiddenEventEntity(val eventId: String, val epochDay: Int) {

    fun idWithoutRID() = eventId.substringBeforeLast("/RID=")

}