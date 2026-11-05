package presentation.screens.main

import androidx.lifecycle.viewModelScope
import data.local.LaunchCounterDataSource
import domain.use_case.AppThemeListenerUseCase
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import platform.EventEditViewHandler
import platform.ModalPresenter

class AppViewModel(
    private val appThemeListenerUseCase: AppThemeListenerUseCase,
    private val eventEditViewHandler: EventEditViewHandler,
    private val modalPresenter: ModalPresenter,
    private val launchCounterDataSource: LaunchCounterDataSource
) : BaseViewModel<Event, State, Effect>() {

    init {
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

            Event.NewEvent -> showNewEventScreen()
            Event.DismissOnboarding -> setEffect { Effect.CloseOnboardingAndGoToCalendar }
            Event.OnOpenDeviceSettings -> setEffect { Effect.OpenDeviceAppSettings }
        }
    }

    private fun showNewEventScreen() {
        modalPresenter.presentAddEvent(
            appTheme = uiState.value.appTheme,
            onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
    }
}