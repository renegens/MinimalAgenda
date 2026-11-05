package di

import data.DataStore
import data.reminder.ScheduledReminderStore
import platform.ReminderScheduler
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.ActivityEditEventView
import platform.AndroidCalendarPermissionHandler
import platform.AndroidModalPresenter
import platform.AndroidNotificationHandler
import platform.AndroidNotificationPermissionHandler
import platform.Browser
import platform.CalendarPermissionHandler
import platform.EventEditViewHandler
import platform.ModalPresenter
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import platform.PermissionRequester
import repository.CalendarRepository
import repository.AndroidCalendarRepository

actual val platformModule: Module = module {
    single { DataStore(get()) }
    factory { ActivityEditEventView(get()) }.bind<EventEditViewHandler>()
    single { ScheduledReminderStore(get()) }
    single { ReminderScheduler(get()) }
    single { AndroidNotificationHandler(get(), get(), get(), get()) }.bind<NotificationHandler>()
    single { AndroidNotificationPermissionHandler(get(), get()) }.bind<NotificationPermissionHandler>()
    single { AndroidCalendarRepository(get()) }.bind<CalendarRepository>()
    single { PermissionRequester() }
    single { AndroidCalendarPermissionHandler(get(), get()) }.bind<CalendarPermissionHandler>()
    factory { Browser(get()) }
    factory { AndroidModalPresenter(get()) }.bind<ModalPresenter>()
}