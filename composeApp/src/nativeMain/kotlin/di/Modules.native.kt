package di

import data.DataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.CalendarPermissionHandler
import platform.EventKit
import platform.iOSCalendarPermissionHandler
import repository.CalendarRepository
import repository.iOSCalendarRepository

actual val platformModule = module {
    single { iOSCalendarRepository(get()) }.bind<CalendarRepository>()
    single { DataStore() }
    singleOf(::EventKit)
    single { iOSCalendarPermissionHandler(get()) }.bind<CalendarPermissionHandler>()
}