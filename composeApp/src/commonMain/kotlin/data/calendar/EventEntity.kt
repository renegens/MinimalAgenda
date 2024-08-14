package data.calendar

data class EventEntity(
    val id: String?,
    val title: String?,
    val startTime: Long?,
    var epochDay: Int? = null
)