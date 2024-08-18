package di

import data.local.HiddenEventsLocalDataSource
import domain.use_case.CalendarGenerator
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.screens.calendar.CalendarViewModel
import domain.use_case.GetAllEventsUseCase
import domain.use_case.ScheduleNotificationUseCase
import domain.use_case.ToggleEventVisibilityUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import repository.HiddenEventsRepository
import repository.HiddenEventsRepositoryImpl

expect val platformModule: Module

val sharedModule = module {
    singleOf(::CalendarGenerator)
    singleOf(::GetAllEventsUseCase)
    factory { ToggleEventVisibilityUseCase(get()) }
    factoryOf(::ScheduleNotificationUseCase)
    single { HiddenEventsLocalDataSource(get()) }
    single { HiddenEventsRepositoryImpl(get()) }.bind<HiddenEventsRepository>()
    viewModelOf(::CalendarViewModel)
}