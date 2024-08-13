package platform

class AndroidCalendarPermissionHandler : CalendarPermissionHandler {
    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State {
        return CalendarPermissionHandler.State.FullAccess
    }

    override fun getPermissionState(): CalendarPermissionHandler.State {
        return CalendarPermissionHandler.State.FullAccess
    }
}