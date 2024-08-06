package data

data class EventEntity(
    val id: Long?,
    val title: String?,
    val description: String?,
    val startTime: Long?,
    val endTime: Long?,
    val epochDay: Int?,
)