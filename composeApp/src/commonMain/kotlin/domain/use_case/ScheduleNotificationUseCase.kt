package domain.use_case

import domain.model.AgendaEvent
import kotlinx.datetime.Clock
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import kotlin.time.Duration.Companion.minutes

class ScheduleNotificationUseCase(
    private val notificationHandler: NotificationHandler,
    private val notificationPermissionHandler: NotificationPermissionHandler,
    private val getSettingsUseCase: GetSettingsUseCase
) {

    sealed class State {
        object Success : State()
        object Fail : State()
        object PermissionMissing : State()
    }

    suspend operator fun invoke(agendaEvent: AgendaEvent): State {
        val permissionGranted = notificationPermissionHandler.requestNotificationPermission()
        if (permissionGranted) {

            val settings = getSettingsUseCase()

            val notificationAlert = settings.notificationTimeInMinutes

            var delayInSeconds =
                agendaEvent.startTime - Clock.System.now().epochSeconds - notificationAlert.minutes.inWholeSeconds

            if (delayInSeconds < 0) {
                delayInSeconds = 1
            }

            val title = if (notificationAlert > 0) {
                "Event in ${notificationAlert.minutes}"
            } else {
                "Event Starting Now"
            }

            val body = if (settings.privateNotifications) {
                "You have an upcoming event."
            } else {
                agendaEvent.title
            }

            val didSchedule = notificationHandler.scheduleNotification(
                id = agendaEvent.id,
                title = title,
                body = body,
                delayInSeconds = delayInSeconds.toInt()
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
