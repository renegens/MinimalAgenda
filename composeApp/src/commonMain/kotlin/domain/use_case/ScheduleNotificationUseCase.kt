package domain.use_case

import domain.model.AgendaEvent
import kotlinx.datetime.Clock
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import kotlin.time.Duration.Companion.minutes

class ScheduleNotificationUseCase(
    private val notificationHandler: NotificationHandler,
    private val notificationPermissionHandler: NotificationPermissionHandler
) {

    sealed class State {
        object Success : State()
        object Fail : State()
        object PermissionMissing : State()
    }

    suspend operator fun invoke(agendaEvent: AgendaEvent): State {
        val permissionGranted = notificationPermissionHandler.requestNotificationPermission()
        if (permissionGranted) {
            var delayInSeconds = agendaEvent.startTime - Clock.System.now().epochSeconds - 5.minutes.inWholeSeconds

            if (delayInSeconds < 0 ){
                delayInSeconds = 1
            }

            val didSchedule = notificationHandler.scheduleNotification(
                id = agendaEvent.id,
                title = "Event reminder",
                body = "Your next event starts in 5 minutes.",
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
