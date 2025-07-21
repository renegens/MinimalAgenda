package presentation.screens.main

import domain.model.AgendaEvent
import domain.model.AppTheme
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
        object PresentPaywall : Effect()
        object DismissPaywall : Effect()
        object CloseOnboardingAndGoToCalendar: Effect()
        object OpenDeviceAppSettings: Effect()
    }

    sealed class Event : UiEvent {
        class EditExternal(val agendaEvent: AgendaEvent) : Event()
        object NewEvent : Event()
        object OnOpenDeviceSettings: Event()
        object NotEligible : Event()
        object OnDismissPaywall : Event()
        object OnPurchased : Event()
        object CheckPaywall : Event()
        object DismissOnboarding : Event()
    }
}
