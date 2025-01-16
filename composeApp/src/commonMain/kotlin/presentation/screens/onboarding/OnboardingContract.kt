package presentation.screens.onboarding

import domain.model.AppTheme
import platform.CalendarPermissionHandler
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class OnboardingContract {

    data class State(
        val appTheme: AppTheme = AppTheme.Default,
        val calendarPermissionState: CalendarPermissionHandler.State = CalendarPermissionHandler.State.NotDetermined
    ) : UiState

    sealed class Effect : UiEffect {
        object CloseOnboarding : Effect()
    }

    sealed class Event : UiEvent {
        object OnRequestCalendarPermission : Event()
        object OnOpenSettings : Event()
        object OnDismissOnboarding : Event()
    }
}