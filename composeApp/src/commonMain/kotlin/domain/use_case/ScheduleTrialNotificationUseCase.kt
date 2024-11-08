package domain.use_case

import platform.NotificationHandler
import platform.NotificationPermissionHandler

class ScheduleTrialNotificationUseCase(
    private val notificationHandler: NotificationHandler,
    private val notificationPermissionHandler: NotificationPermissionHandler,
) {

    sealed class State {
        object Success : State()
        object Fail : State()
        object PermissionMissing : State()
    }

    suspend operator fun invoke(): State {
        val permissionGranted = notificationPermissionHandler.requestNotificationPermission()
        if (permissionGranted) {

            val fiveDaysSeconds = 432000
            val didSchedule = notificationHandler.scheduleNotification(
                id = "daily planner trial reminder",
                title = "Daily Planner",
                body = "Your trial will end in 2 days",
                delayInSeconds = fiveDaysSeconds
            )

            return if (didSchedule) {
                State.Success
            } else {
                State.Fail
            }
        }
        return State.PermissionMissing
    }
}
