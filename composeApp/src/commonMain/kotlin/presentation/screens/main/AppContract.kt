package presentation.screens.main

import domain.model.AgendaEvent
import domain.model.AppTheme
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AppContract {

    data class State(
        val forceEventLoad: Int = 0,
        val appTheme: AppTheme = AppTheme.Default
    ) : UiState

    sealed class Effect : UiEffect

    sealed class Event : UiEvent {
        class EditExternal(val agendaEvent: AgendaEvent): Event()
        object NewEvent: Event()
        object NotEligible: Event()
    }
}
