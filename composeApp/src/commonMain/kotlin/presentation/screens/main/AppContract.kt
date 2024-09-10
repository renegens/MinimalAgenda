package presentation.screens.main

import domain.model.AppTheme
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AppContract {

    data class State(
        val isSubscribed: Boolean = false,
        val appTheme: AppTheme? = null
    ) : UiState

    sealed class Effect : UiEffect

    sealed class Event : UiEvent {
        object OnPurchaseCompleted : Event()
    }
}
