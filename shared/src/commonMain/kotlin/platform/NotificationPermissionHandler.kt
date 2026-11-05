package platform

interface NotificationPermissionHandler {
    suspend fun requestNotificationPermission(): Boolean
}