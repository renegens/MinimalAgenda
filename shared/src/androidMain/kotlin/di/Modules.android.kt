package di

import data.DataStore
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
    factory {ActivityEditEventView()}.bind<EventEditViewHandler>()
    single { AndroidNotificationHandler() }.bind<NotificationHandler>()
    single { AndroidNotificationPermissionHandler() }.bind<NotificationPermissionHandler>()
    single { AndroidCalendarRepository(get()) }.bind<CalendarRepository>()
    single { PermissionRequester() }
    single { AndroidCalendarPermissionHandler(get(), get()) }.bind<CalendarPermissionHandler>()
    factory { Browser(get()) }
    factory { AndroidModalPresenter() }.bind<ModalPresenter>()
}