package di

import domain.use_case.CalendarGenerator
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.screens.calendar.CalendarViewModel
import domain.use_case.GetAllEventsUseCase
import domain.use_case.EventsPerMonthUseCase
import org.koin.core.module.dsl.singleOf

expect val platformModule: Module

val sharedModule = module {
    singleOf(::CalendarGenerator)
    singleOf(::GetAllEventsUseCase)
    singleOf(::EventsPerMonthUseCase)
    viewModelOf(::CalendarViewModel)
}