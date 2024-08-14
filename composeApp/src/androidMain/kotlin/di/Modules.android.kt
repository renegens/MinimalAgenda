package di

import data.DataStore
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.AndroidCalendarPermissionHandler
import platform.CalendarPermissionHandler
import repository.CalendarRepository
import repository.AndroidCalendarRepository

actual val platformModule: Module = module {
    single { DataStore(get()) }
    single { AndroidCalendarRepository(get()) }.bind<CalendarRepository>()
    single { AndroidCalendarPermissionHandler() }.bind<CalendarPermissionHandler>()
}