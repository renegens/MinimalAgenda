package presentation.screens.main

import domain.model.AgendaEvent
import domain.model.AppTheme
import kotlinx.datetime.LocalDate
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AppContract {

    data class State(
        val forceEventLoad: Int = 0,
        val appTheme: AppTheme = AppTheme.Default,
        val shouldShowOnboarding: Boolean = false,
    ) : UiState

    sealed class Effect : UiEffect {
        object CloseOnboardingAndGoToCalendar: Effect()
        object OpenDeviceAppSettings: Effect()
    }

    sealed class Event : UiEvent {
        class EditExternal(val agendaEvent: AgendaEvent) : Event()
        class NewEvent(val date: LocalDate? = null) : Event()
        object OnOpenDeviceSettings: Event()
        object DismissOnboarding : Event()
    }
}
