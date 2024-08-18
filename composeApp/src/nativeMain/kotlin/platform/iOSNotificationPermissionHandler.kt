package platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@Suppress("ClassName")
class iOSNotificationPermissionHandler : NotificationPermissionHandler {
    override suspend fun requestNotificationPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val center = UNUserNotificationCenter.currentNotificationCenter()
            center.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, error ->
                if (error != null) {
                    continuation.resume(false)
                } else {
                    continuation.resume(granted)
                }
            }
        }
    }
}