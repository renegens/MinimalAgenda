package platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import data.reminder.ScheduledReminderStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

/**
 * Re-registers every persisted reminder after a reboot, app update or clock/time zone change (alarms are
 * lost in the first two). Overdue reminders are scheduled to fire in one second; the fire-time re-check
 * then shows them once, or drops them when the event has already ended.
 */
class ReminderRestoreReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                ReminderScheduler(context.applicationContext)
                    .restoreAll(KoinPlatform.getKoin().get<ScheduledReminderStore>())
            } catch (_: Exception) {
                // Never crash from a background broadcast.
            } finally {
                pending.finish()
            }
        }
    }
}
