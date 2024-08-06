package di

import domain.use_case.CalendarGenerator
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import presentation.screens.calendar.CalendarViewModel
import domain.use_case.CalendarWithEventsUseCase

expect val platformModule: Module

val sharedModule = module {
    factoryOf(::CalendarWithEventsUseCase)
    factoryOf(::CalendarGenerator)
    viewModelOf(::CalendarViewModel)
}