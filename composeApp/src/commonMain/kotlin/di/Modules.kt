package di

import data.CalendarGenerator
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import presentation.screens.main.MainViewModel
import use_case.CalendarWithEvents

expect val platformModule: Module

val sharedModule = module {
    factoryOf(::CalendarWithEvents)
    factoryOf(::CalendarGenerator)
    viewModelOf(::MainViewModel)
}