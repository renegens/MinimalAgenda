package platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import data.reminder.ScheduledReminder
import data.reminder.ScheduledReminderStore
import kotlin.time.Clock

/** Sets/cancels the AlarmManager alarm of a reminder. The same id always maps to the same PendingIntent. */
class ReminderScheduler(private val context: Context) {

    private val alarmManager get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: ScheduledReminder, triggerAtSeconds: Long = reminder.triggerAtSeconds) {
        val triggerAtMillis = triggerAtSeconds * 1000L
        val pending = pendingIntent(reminder.id, PendingIntent.FLAG_UPDATE_CURRENT)!!
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        }
    }

    /** Re-registers every persisted reminder; overdue ones are set to fire in one second (the fire-time re-check decides). */
    suspend fun restoreAll(store: ScheduledReminderStore) {
        val now = Clock.System.now().epochSeconds
        store.getAll().forEach { schedule(it, maxOf(it.triggerAtSeconds, now + 1)) }
    }

    fun cancel(id: String) {
        pendingIntent(id, PendingIntent.FLAG_NO_CREATE)?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun pendingIntent(id: String, flag: Int): PendingIntent? {
        val intent = Intent(context, ReminderReceiver::class.java)
            .setAction(ReminderReceiver.ACTION_FIRE)
            .setData(Uri.fromParts("reminder", id, null))
            .putExtra(ReminderReceiver.EXTRA_ID, id)
        return PendingIntent.getBroadcast(context, id.hashCode(), intent, flag or PendingIntent.FLAG_IMMUTABLE)
    }
}
