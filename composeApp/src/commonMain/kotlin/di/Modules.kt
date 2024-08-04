package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.screens.main.MainViewModel

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::MainViewModel)
}