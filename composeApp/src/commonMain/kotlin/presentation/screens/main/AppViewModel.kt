package presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.models.PackageType
import data.local.LaunchCounterDataSource
import domain.use_case.AppThemeListenerUseCase
import domain.use_case.SavePaywallTimestampUseCase
import domain.use_case.ShowPaywallOncePerDayUseCase
import extensions.guardPremium
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import extensions.initializeRevenueCat
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import platform.CalendarPermissionHandler
import platform.EventEditViewHandler
import platform.ModalPresenter

class AppViewModel(
    private val appThemeListenerUseCase: AppThemeListenerUseCase,
    private val eventEditViewHandler: EventEditViewHandler,
    private val modalPresenter: ModalPresenter,
    private val showPaywallOncePerDayUseCase: ShowPaywallOncePerDayUseCase,
    private val savePaywallTimestampUseCase: SavePaywallTimestampUseCase,
    private val launchCounterDataSource: LaunchCounterDataSource,
    private val permissionHandler: CalendarPermissionHandler
) : BaseViewModel<Event, State, Effect>() {

    init {
        initializeRevenueCat()
        viewModelScope.launch {
            runBlocking {
                try {
                    withTimeout(1000L) {
                        if (launchCounterDataSource.getCount() == 0) {
                            setState { copy(shouldShowOnboarding = true) }
                        }
                        launchCounterDataSource.increment()
                    }
                } catch (e: TimeoutCancellationException) {
                    println("Task timed out: ${e.message}")
                }
            }
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
                guardPremiumLetThroughDemo(
                    onSubscribed = ::showNewEventScreen,
                    onPaywall = { setEvent(Event.NotEligible) })

            Event.NotEligible -> {
                viewModelScope.launch { savePaywallTimestampUseCase() }
                setEffect { Effect.PresentPaywall }
            }

            Event.OnDismissPaywall -> setEffect { Effect.DismissPaywall }
            is Event.OnPurchased -> setEffect { Effect.DismissPaywall }
            Event.CheckPaywall -> viewModelScope.launch {
                if (showPaywallOncePerDayUseCase.shouldPresentPaywall()) {
                    setEffect { Effect.PresentPaywall }
                }
            }
            Event.DismissOnboarding -> setEffect { Effect.CloseOnboardingAndGoToCalendar }
            Event.OnOpenDeviceSettings -> setEffect { Effect.OpenDeviceAppSettings }
        }
    }

    private fun showNewEventScreen() {
        modalPresenter.presentAddEvent(
            appTheme = uiState.value.appTheme,
            onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
    }

    private fun guardPremiumLetThroughDemo(onSubscribed: () -> Unit, onPaywall: () -> Unit) {
        if (permissionHandler.getPermissionState() == CalendarPermissionHandler.State.FullAccess) {
            guardPremium(onSubscribed = onSubscribed, onPaywall = onPaywall)
        } else {
            onSubscribed.invoke()
        }
    }
}