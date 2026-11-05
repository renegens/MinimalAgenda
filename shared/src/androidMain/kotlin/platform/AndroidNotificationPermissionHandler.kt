package platform

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

class AndroidNotificationPermissionHandler(
    private val context: Context,
    private val requester: PermissionRequester,
) : NotificationPermissionHandler {

    override suspend fun requestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return true
        val result = requester.request(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        return result?.get(Manifest.permission.POST_NOTIFICATIONS) == true
    }
}
