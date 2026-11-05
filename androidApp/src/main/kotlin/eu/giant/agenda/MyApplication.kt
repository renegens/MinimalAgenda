package eu.giant.agenda

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import data.reminder.ScheduledReminderStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import platform.AppConfig
import platform.ReminderScheduler

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(module {
                single<AppConfig> { object : AppConfig { override val showDonate = BuildConfig.SHOW_DONATE
                    override val showFocusModeSetup = false
                } }
            })
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
