package platform

interface CalendarPermissionHandler {
    suspend fun requestCalendarPermission(): State
    fun getPermissionState(): State

    enum class State {
        FullAccess,
        Denied,
        NotDetermined
    }
}