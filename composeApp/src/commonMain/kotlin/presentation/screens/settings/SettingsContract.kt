package presentation.screens.settings

import domain.model.AppTheme
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class SettingsContract {

    data class State(
        val isLoading: Boolean = true,
        val appTheme: AppTheme = AppTheme.FollowSystem,
        val privateNotifications: Boolean = true,
        val notificationTime: Int = 5
    ) : UiState

    sealed class Effect : UiEffect {}

    sealed class Event : UiEvent {
        object Load : Event()
        class OnTogglePrivateNotifications(val checked: Boolean) : Event()
        class OnModeSelected(val appTheme: AppTheme) : Event()
        object OnSentFeedback : Event()
        class OnNotificationTimeEntered(val time: Int) : Event()
    }
}