package domain.use_case

import platform.CalendarPermissionHandler

class ShouldShowDemoModeUseCase(
    private val permissionHandler: CalendarPermissionHandler
) {

    operator fun invoke() = when (permissionHandler.getPermissionState()) {
        CalendarPermissionHandler.State.FullAccess -> false
        CalendarPermissionHandler.State.Denied -> true
        CalendarPermissionHandler.State.NotDetermined -> true
    }

}