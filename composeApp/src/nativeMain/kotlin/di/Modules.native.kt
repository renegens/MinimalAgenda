package di

import org.koin.dsl.bind
import org.koin.dsl.module
import repository.CalendarRepository
import repository.iOSCalendarRepository

actual val platformModule = module {
    single { iOSCalendarRepository() }.bind<CalendarRepository>()
}