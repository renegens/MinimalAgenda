package eu.giant.agenda

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import data.reminder.ScheduledReminderStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import platform.ReminderScheduler

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
            androidLogger()
        }

        // Alarms are lost on force-stop; re-register persisted reminders whenever the app process starts.
        CoroutineScope(Dispatchers.Default).launch {
            try {
                ReminderScheduler(this@MyApplication).restoreAll(KoinPlatform.getKoin().get<ScheduledReminderStore>())
            } catch (_: Exception) {
            }
        }
    }
}
