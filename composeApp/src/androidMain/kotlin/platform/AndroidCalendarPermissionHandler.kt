package platform

class AndroidCalendarPermissionHandler : CalendarPermissionHandler {
    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State {
        return CalendarPermissionHandler.State.NotDetermined
    }

    override fun getPermissionState(): CalendarPermissionHandler.State {
        return CalendarPermissionHandler.State.FullAccess
    }
}