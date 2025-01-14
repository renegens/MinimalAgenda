package presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.models.PackageType
import data.local.LaunchCounterDataSource
import domain.use_case.AppThemeListenerUseCase
import domain.use_case.SavePaywallTimestampUseCase
import domain.use_case.ScheduleTrialNotificationUseCase
import domain.use_case.ShowPaywallOncePerDayUseCase
import extensions.guardPremium
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import extensions.initializeRevenueCat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import platform.CalendarPermissionHandler
import platform.EventEditViewHandler
import platform.ModalPresenter

class AppViewModel(
    private val appThemeListenerUseCase: AppThemeListenerUseCase,
    private val eventEditViewHandler: EventEditViewHandler,
    private val modalPresenter: ModalPresenter,
    private val scheduleTrialNotificationUseCase: ScheduleTrialNotificationUseCase,
    private val showPaywallOncePerDayUseCase: ShowPaywallOncePerDayUseCase,
    private val savePaywallTimestampUseCase: SavePaywallTimestampUseCase,
    private val launchCounterDataSource: LaunchCounterDataSource,
    private val calendarPermissionHandler: CalendarPermissionHandler
) : BaseViewModel<Event, State, Effect>() {

    init {
        initializeRevenueCat()
        viewModelScope.launch {
            if (launchCounterDataSource.getCount() > 0) {
                setState { copy(shouldShowOnboarding = true) }
            }
            launchCounterDataSource.increment()
            appThemeListenerUseCase().collectLatest {
                setState { copy(appTheme = it) }
            }
        }
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.EditExternal -> viewModelScope.launch {
                eventEditViewHandler.openEdit(agendaEvent = event.agendaEvent,
                    onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
            }

            Event.NewEvent ->
                guardPremium(
                    onSubscribed = ::showNewEventScreen,
                    onPaywall = { setEvent(Event.NotEligible) })

            Event.NotEligible -> {
                viewModelScope.launch { savePaywallTimestampUseCase() }
                setEffect { Effect.PresentPaywall }
            }

            Event.OnDismissPaywall -> setEffect { Effect.DismissPaywall }
            is Event.OnPurchased -> {
                setEffect { Effect.DismissPaywall }
                if (event.purchase?.packageType == PackageType.ANNUAL) {
                    setState { copy(showNotificationPermissionInfo = true) }
                }
            }

            Event.DismissTrialNotification -> setState {
                copy(
                    showTrialNotificationSuccess = false,
                    showTrialNotificationError = false
                )
            }

            Event.CheckPaywall -> viewModelScope.launch {
                if (showPaywallOncePerDayUseCase.shouldPresentPaywall()) {
                    setEffect { Effect.PresentPaywall }
                }
            }

            is Event.RequestNotificationPermission -> {
                setState { copy(showNotificationPermissionInfo = false) }
                if (event.shouldRequest) {
                    viewModelScope.launch {
                        when (scheduleTrialNotificationUseCase()) {
                            ScheduleTrialNotificationUseCase.State.Success -> setState {
                                copy(
                                    showTrialNotificationSuccess = true
                                )
                            }

                            else -> setState { copy(showTrialNotificationError = true) }
                        }
                    }
                } else {
                    setState { copy(showTrialNotificationError = true) }
                }
            }

            Event.DismissOnboarding -> setState { copy(shouldShowOnboarding = false) }
            Event.RequestCalendarPermission -> viewModelScope.launch {
                when (val state = calendarPermissionHandler.requestCalendarPermission()) {
                    CalendarPermissionHandler.State.FullAccess -> {}
                    else -> {}
                }
            }
        }
    }

    private fun showNewEventScreen() {
        modalPresenter.presentAddEvent(
            appTheme = uiState.value.appTheme,
            onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
    }
}