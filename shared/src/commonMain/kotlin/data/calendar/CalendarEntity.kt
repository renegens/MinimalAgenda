package data.calendar

data class CalendarEntity(
    val id: String?,
    val name: String,
    val accountName: String,
    val accountType: String,
    /** The user can add/change events here (Android: access level >= contributor). Always false on iOS. */
    val canWrite: Boolean = false,
    val isPrimary: Boolean = false,
)