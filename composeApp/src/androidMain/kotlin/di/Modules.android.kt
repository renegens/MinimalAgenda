package di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import repository.CalendarRepository
import repository.CalendarRepositoryImpl

actual val platformModule: Module = module {
    single { CalendarRepositoryImpl(get()) }.bind<CalendarRepository>()
}