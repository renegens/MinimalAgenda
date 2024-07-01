package presentation.screens.main

import data.Calendar
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState


class MainContract {

    data class State(
        val calendar: Calendar?
    ) : UiState

    sealed class Effect : UiEffect

    sealed class Event : UiEvent {
        object LoadData : Event()
    }
}
