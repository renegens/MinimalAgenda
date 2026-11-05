package platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationInterruptionLevel
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@Suppress("ClassName")
class iOSNotificationHandler : NotificationHandler {

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayInSeconds: Int
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val content = UNMutableNotificationContent().apply {
                setTitle(title)
                setBody(body)
                setInterruptionLevel(UNNotificationInterruptionLevel.UNNotificationInterruptionLevelTimeSensitive)
                setSound(UNNotificationSound.defaultSound())
            }

            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                delayInSeconds.toDouble(),
                false
            )
            val request = UNNotificationRequest.requestWithIdentifier(
                id, content, trigger
            )

            UNUserNotificationCenter.currentNotificationCenter()
                .addNotificationRequest(request) { error ->
                    if (error != null) {
                        continuation.resume(false)
                    } else {
                        continuation.resume(true)
                    }
                }
        }
    }
}