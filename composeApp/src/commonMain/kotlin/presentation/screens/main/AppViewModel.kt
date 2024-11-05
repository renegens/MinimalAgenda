package presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.PackageType
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
import platform.EventEditViewHandler
import platform.ModalPresenter

class AppViewModel(
    private val appThemeListenerUseCase: AppThemeListenerUseCase,
    private val eventEditViewHandler: EventEditViewHandler,
    private val modalPresenter: ModalPresenter,
    private val scheduleTrialNotificationUseCase: ScheduleTrialNotificationUseCase,
    private val showPaywallOncePerDayUseCase: ShowPaywallOncePerDayUseCase,
    private val savePaywallTimestampUseCase: SavePaywallTimestampUseCase
) : BaseViewModel<Event, State, Effect>() {

    init {
        initializeRevenueCat()
        Purchases.sharedInstance.getCustomerInfo(onError = { }, onSuccess = { })

        viewModelScope.launch {
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
                setState { copy(showPaywall = true) }
            }

            Event.DismissPaywall -> setState { copy(showPaywall = false) }
            is Event.OnPurchased -> {
                setState { copy(showPaywall = false) }
                if (event.purchase?.packageType == PackageType.ANNUAL) {
                    viewModelScope.launch {
                        when (scheduleTrialNotificationUseCase()) {
                            ScheduleTrialNotificationUseCase.State.Success -> setState {
                                copy(
                                    showTrialSuccess = true
                                )
                            }

                            else -> setState { copy(showTrialError = true) }
                        }
                    }
                }
            }

            Event.DismissTrialNotification -> setState {
                copy(
                    showTrialSuccess = false,
                    showTrialError = false
                )
            }

            Event.CheckPaywall -> viewModelScope.launch {
                if (showPaywallOncePerDayUseCase.shouldPresentPaywall()) {
                    setState { copy(showPaywall = true) }
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