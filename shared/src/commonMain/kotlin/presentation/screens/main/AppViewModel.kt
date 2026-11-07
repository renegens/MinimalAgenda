package presentation.screens.main

import androidx.lifecycle.viewModelScope
import data.local.LaunchCounterDataSource
import domain.use_case.AppThemeListenerUseCase
import presentation.base.BaseViewModel
import presentation.screens.main.AppContract.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.IOException
import platform.EventEditViewHandler
import platform.ModalPresenter
import kotlinx.datetime.LocalDate

class AppViewModel(
    private val appThemeListenerUseCase: AppThemeListenerUseCase,
    private val eventEditViewHandler: EventEditViewHandler,
    private val modalPresenter: ModalPresenter,
    private val launchCounterDataSource: LaunchCounterDataSource
) : BaseViewModel<Event, State, Effect>() {

    init {
        viewModelScope.launch {
            val firstLaunch = launchCounterDataSource.getCount() == 0
            try {
                launchCounterDataSource.increment()
            } catch (_: IOException) {
                // A temporarily unavailable preferences file must not block opening the app.
            }
            setState { copy(isLoading = false, shouldShowOnboarding = firstLaunch) }
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

            is Event.NewEvent -> showNewEventScreen(event.date)
            Event.DismissOnboarding -> setEffect { Effect.CloseOnboardingAndGoToCalendar }
            Event.OnOpenDeviceSettings -> setEffect { Effect.OpenDeviceAppSettings }
        }
    }

    private fun showNewEventScreen(date: LocalDate?) {
        modalPresenter.presentAddEvent(
            appTheme = uiState.value.appTheme,
            date = date,
            onSave = { setState { copy(forceEventLoad = uiState.value.forceEventLoad + 1) } })
    }
}
