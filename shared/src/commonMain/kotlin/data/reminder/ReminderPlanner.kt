package data.reminder

import kotlinx.serialization.Serializable

/** A reminder that was scheduled with the OS and persisted so it can be restored and re-checked. Times are epoch seconds. */
@Serializable
data class ScheduledReminder(
    val id: String,
    val title: String,
    val body: String,
    val triggerAtSeconds: Long,
    val offsetMinutes: Int,
    /** Start/end of the event instance when the reminder was scheduled (or last rescheduled). */
    val startSeconds: Long,
    val endSeconds: Long,
)

/** Apply today's privacy preference at delivery, even if the reminder originally included the event name. */
fun ScheduledReminder.bodyForDelivery(privateNotifications: Boolean, currentTitle: String? = null): String =
    if (privateNotifications) "You have an upcoming event." else currentTitle ?: body

/** Start and end of an event, epoch seconds. */
data class EventTimes(val startSeconds: Long, val endSeconds: Long)

/** The underlying (non-instance) event as found by its event id. */
data class EventLookup(val times: EventTimes, val isRecurring: Boolean)

sealed interface ReminderOutcome {
    data object Show : ReminderOutcome
    data object Drop : ReminderOutcome
    data class Reschedule(val newTriggerAtSeconds: Long, val newTimes: EventTimes) : ReminderOutcome
}

/** Pure decision logic for a reminder whose alarm just fired (or was overdue after a reboot). */
object ReminderPlanner {

    /** An event is considered over this long after its end (covers zero-length events and delayed alarms). */
    const val ENDED_GRACE_SECONDS = 5 * 60L

    /**
     * @param instance the instance with the stored id, or null when it no longer exists
     * @param event the event with the same event id (any begin), or null when it was deleted
     */
    fun decide(
        reminder: ScheduledReminder,
        instance: EventTimes?,
        event: EventLookup?,
        nowSeconds: Long,
    ): ReminderOutcome {
        if (instance != null) {
            return resolve(reminder, instance, nowSeconds)
        }
        // Instance gone: only a moved, non-recurring event can be followed.
        if (event == null || event.isRecurring) return ReminderOutcome.Drop
        return resolve(reminder, event.times, nowSeconds)
    }

    private fun resolve(reminder: ScheduledReminder, times: EventTimes, now: Long): ReminderOutcome {
        if (now > maxOf(times.endSeconds, times.startSeconds) + ENDED_GRACE_SECONDS) return ReminderOutcome.Drop
        if (times.startSeconds == reminder.startSeconds) return ReminderOutcome.Show
        return ReminderOutcome.Reschedule(times.startSeconds - reminder.offsetMinutes * 60L, times)
    }
}
