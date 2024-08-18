package platform

interface NotificationHandler {
    suspend fun scheduleNotification(id: String, title: String, body: String, delayInSeconds: Int): Boolean
}