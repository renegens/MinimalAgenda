package platform

import data.reminder.ScheduledReminder
import data.reminder.ScheduledReminderStore
import domain.use_case.GetSettingsUseCase
import repository.AndroidCalendarRepository
import kotlin.time.Clock

/** Schedules an AlarmManager alarm and persists the reminder so it survives reboots and can be re-checked. */
class AndroidNotificationHandler(
    private val scheduler: ReminderScheduler,
    private val store: ScheduledReminderStore,
    private val repository: AndroidCalendarRepository,
    private val getSettingsUseCase: GetSettingsUseCase,
) : NotificationHandler {

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        delayInSeconds: Int
    ): Boolean = try {
        val trigger = Clock.System.now().epochSeconds + delayInSeconds
        val offset = getSettingsUseCase().notificationTimeInMinutes
        val event = repository.getEvent(id)
        val start = event?.startTime ?: (trigger + offset * 60L)
        val reminder = ScheduledReminder(
            id = id,
            title = title,
            body = body,
            triggerAtSeconds = trigger,
            offsetMinutes = offset,
            startSeconds = start,
            endSeconds = event?.endTime ?: start,
        )
        store.save(reminder)
        scheduler.schedule(reminder)
        true
    } catch (_: Exception) {
        false
    }
}
