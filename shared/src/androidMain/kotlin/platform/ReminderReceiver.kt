package platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import data.calendar.CalendarMath
import data.reminder.EventTimes
import data.reminder.ReminderOutcome
import data.reminder.ReminderPlanner
import data.reminder.ScheduledReminderStore
import data.reminder.bodyForDelivery
import domain.use_case.GetSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import repository.AndroidCalendarRepository
import kotlin.time.Clock

/** Fires when a reminder alarm goes off: re-checks the event, then shows, reschedules or drops the reminder. */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FIRE) return
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                handle(context.applicationContext, id)
            } catch (_: Exception) {
                // Never crash from a background broadcast.
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun handle(context: Context, id: String) {
        val koin = KoinPlatform.getKoin()
        val store = koin.get<ScheduledReminderStore>()
        val repository = koin.get<AndroidCalendarRepository>()
        val scheduler = ReminderScheduler(context)
        val reminder = store.get(id) ?: return
        val now = Clock.System.now().epochSeconds

        val instanceEvent = repository.getEvent(id)
        val instance = instanceEvent?.let { e ->
            val start = e.startTime ?: return@let null
            EventTimes(start, e.endTime ?: start)
        }
        val event = CalendarMath.parseInstanceId(id)?.let { repository.getEventLookup(it.eventId) }

        when (val outcome = ReminderPlanner.decide(reminder, instance, event, now)) {
            ReminderOutcome.Show -> {
                val settings = koin.get<GetSettingsUseCase>()()
                ReminderNotifier(context).show(reminder.copy(
                    body = reminder.bodyForDelivery(settings.privateNotifications, instanceEvent?.title),
                ))
                store.remove(id)
            }
            ReminderOutcome.Drop -> store.remove(id)
            is ReminderOutcome.Reschedule -> {
                val updated = reminder.copy(
                    triggerAtSeconds = outcome.newTriggerAtSeconds,
                    startSeconds = outcome.newTimes.startSeconds,
                    endSeconds = outcome.newTimes.endSeconds,
                )
                store.save(updated)
                // Already due: fire again in a second (the re-check then shows it).
                scheduler.schedule(updated, maxOf(updated.triggerAtSeconds, now + 1))
            }
        }
    }

    companion object {
        const val ACTION_FIRE = "eu.giant.agenda.action.FIRE_REMINDER"
        const val EXTRA_ID = "reminder_id"
    }
}
