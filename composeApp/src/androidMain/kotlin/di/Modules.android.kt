package di

import data.DataStore
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.AndroidCalendarPermissionHandler
import platform.AndroidNotificationHandler
import platform.AndroidNotificationPermissionHandler
import platform.Browser
import platform.CalendarPermissionHandler
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import repository.CalendarRepository
import repository.AndroidCalendarRepository

actual val platformModule: Module = module {
    single { DataStore(get()) }
    single { AndroidNotificationHandler() }.bind<NotificationHandler>()
    single { AndroidNotificationPermissionHandler() }.bind<NotificationPermissionHandler>()
    single { AndroidCalendarRepository(get()) }.bind<CalendarRepository>()
    single { AndroidCalendarPermissionHandler() }.bind<CalendarPermissionHandler>()
    factory { Browser(get()) }
}