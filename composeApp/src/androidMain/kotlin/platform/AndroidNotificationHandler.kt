package platform

class AndroidNotificationHandler: NotificationHandler {

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayInSeconds: Int
    ): Boolean {
        TODO("Not yet implemented")
    }
}