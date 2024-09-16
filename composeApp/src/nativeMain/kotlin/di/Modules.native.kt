package di

import data.DataStore
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Browser
import platform.CalendarPermissionHandler
import platform.EventEditViewHandler
import platform.EventKit
import platform.EKEventEditViewViewHandler
import platform.NotificationHandler
import platform.NotificationPermissionHandler
import platform.ModalPresenter
import platform.iOSCalendarPermissionHandler
import platform.iOSModalPresenter
import platform.iOSNotificationHandler
import platform.iOSNotificationPermissionHandler
import repository.CalendarRepository
import repository.iOSCalendarRepository

actual val platformModule = module {
    single { iOSCalendarRepository(get()) }.bind<CalendarRepository>()
    single { DataStore() }
    single { iOSNotificationPermissionHandler() }.bind<NotificationPermissionHandler>()
    single { iOSNotificationHandler() }.bind<NotificationHandler>()
    singleOf(::EventKit)
    factoryOf(::Browser)
    factoryOf(::EKEventEditViewViewHandler).bind<EventEditViewHandler>()
    factoryOf(::iOSModalPresenter).bind<ModalPresenter>()
    single { iOSCalendarPermissionHandler(get()) }.bind<CalendarPermissionHandler>()
}