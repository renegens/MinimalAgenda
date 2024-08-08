package di

import org.koin.dsl.bind
import org.koin.dsl.module
import repository.CalendarRepository
import repository.CalendarRepositoryImpl

actual val platformModule = module {
    single { CalendarRepositoryImpl() }.bind<CalendarRepository>()
}