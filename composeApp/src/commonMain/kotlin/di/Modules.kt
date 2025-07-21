package di

import data.local.HiddenEventsLocalDataSource
import data.local.LaunchCounterDataSource
import data.local.PaywallLocalDataSource
import data.local.SettingsLocalDataSource
import data.local.SwipeToolTipDataSource
import domain.use_case.AppThemeListenerUseCase
import domain.use_case.CalendarGenerator
import domain.use_case.FlatCalendarViewFactory
import domain.use_case.GetAllEventsUseCase
import domain.use_case.GetSettingsUseCase
import domain.use_case.SavePaywallTimestampUseCase
import domain.use_case.SaveSettingsUseCase
import domain.use_case.ScheduleNotificationUseCase
import domain.use_case.ShowPaywallOncePerDayUseCase
import domain.use_case.DidSeeSwipeTooltipUseCase
import domain.use_case.ShouldShowDemoModeUseCase
import domain.use_case.ToggleEventVisibilityUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

import presentation.screens.add_event.AddEventViewModel
import presentation.screens.calendar.CalendarViewModel
import presentation.screens.onboarding.OnboardingViewModel
import presentation.screens.main.AppViewModel
import presentation.screens.settings.SettingsViewModel
import repository.HiddenEventsRepository
import repository.HiddenEventsRepositoryImpl
import repository.SettingsRepository
import repository.SettingsRepositoryImpl

expect val platformModule: Module

val sharedModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::AddEventViewModel)
    viewModelOf(::SettingsViewModel)

    singleOf(::CalendarGenerator)

    singleOf(::GetAllEventsUseCase)
    factoryOf(::GetSettingsUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::DidSeeSwipeTooltipUseCase)
    factoryOf(::ScheduleNotificationUseCase)
    factoryOf(::ShowPaywallOncePerDayUseCase)
    factoryOf(::SavePaywallTimestampUseCase)
    singleOf(::AppThemeListenerUseCase)
    factoryOf(::ShouldShowDemoModeUseCase)

    factoryOf(::FlatCalendarViewFactory)

    single { HiddenEventsLocalDataSource(get()) }
    single { HiddenEventsRepositoryImpl(get()) }.bind<HiddenEventsRepository>()

    singleOf(::SettingsLocalDataSource)
    singleOf(::SwipeToolTipDataSource)
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::PaywallLocalDataSource)
    singleOf(::LaunchCounterDataSource)

    factory { ToggleEventVisibilityUseCase(get()) }
}