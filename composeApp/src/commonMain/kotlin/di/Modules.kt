package di

import data.local.HiddenEventsLocalDataSource
import data.local.SettingsLocalDataSource
import domain.use_case.AppThemeListenerUseCase
import domain.use_case.CalendarGenerator
import domain.use_case.FlatCalendarViewFactory
import domain.use_case.GetAllEventsUseCase
import domain.use_case.GetSettingsUseCase
import domain.use_case.SaveSettingsUseCase
import domain.use_case.ScheduleNotificationUseCase
import domain.use_case.ScheduleTrialNotificationUseCase
import domain.use_case.ToggleEventVisibilityUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

import presentation.screens.add_event.AddEventViewModel
import presentation.screens.calendar.CalendarViewModel
import presentation.screens.main.AppViewModel
import presentation.screens.paywall.PaywallViewModel
import presentation.screens.settings.SettingsViewModel
import repository.HiddenEventsRepository
import repository.HiddenEventsRepositoryImpl
import repository.SettingsRepository
import repository.SettingsRepositoryImpl

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::AddEventViewModel)
    viewModelOf(::PaywallViewModel)
    viewModelOf(::SettingsViewModel)

    singleOf(::CalendarGenerator)

    singleOf(::GetAllEventsUseCase)
    factoryOf(::GetSettingsUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::ScheduleNotificationUseCase)
    factoryOf(::ScheduleTrialNotificationUseCase)
    singleOf(::AppThemeListenerUseCase)

    factoryOf(::FlatCalendarViewFactory)

    single { HiddenEventsLocalDataSource(get()) }
    single { HiddenEventsRepositoryImpl(get()) }.bind<HiddenEventsRepository>()

    singleOf(::SettingsLocalDataSource)
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()

    factory { ToggleEventVisibilityUseCase(get()) }
}