package presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import domain.use_case.AppThemeListenerUseCase
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
    private val modalPresenter: ModalPresenter
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
                modalPresenter.presentPaywall(appTheme = uiState.value.appTheme, onCompletion = {})
            }
        }
    }

    private fun showNewEventScreen() {
        modalPresenter.presentAddEvent(
            appTheme = uiState.value.appTheme,
            onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
    }
}