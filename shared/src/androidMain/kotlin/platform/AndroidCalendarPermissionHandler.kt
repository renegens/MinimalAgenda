package platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

class AndroidCalendarPermissionHandler(
    private val context: Context,
    private val requester: PermissionRequester,
) : CalendarPermissionHandler {

    private val prefs get() = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State {
        if (getPermissionState() == CalendarPermissionHandler.State.FullAccess) {
            return CalendarPermissionHandler.State.FullAccess
        }
        val result = requester.request(PERMISSIONS)
        if (result != null) {
            prefs.edit().putBoolean(KEY_ASKED, true).apply()
        }
        return getPermissionState()
    }

    override fun getPermissionState(): CalendarPermissionHandler.State {
        if (PERMISSIONS.all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) {
            return CalendarPermissionHandler.State.FullAccess
        }
        // Not granted. Without a rationale after we already asked, the system won't show the dialog again
        // (permanently denied), so the user has to go to Settings.
        val askedBefore = prefs.getBoolean(KEY_ASKED, false)
        val canAskAgain = PERMISSIONS.any { requester.shouldShowRationale(it) }
        return if (askedBefore && !canAskAgain) {
            CalendarPermissionHandler.State.Denied
        } else {
            CalendarPermissionHandler.State.NotDetermined
        }
    }

    private companion object {
        const val PREFS = "calendar_permission"
        const val KEY_ASKED = "asked"
        val PERMISSIONS = arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
    }
}
