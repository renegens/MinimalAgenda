package presentation.screens.calendar

import data.Calendar
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState


class CalendarContract {

    data class State(
        val calendar: Calendar? = null
    ) : UiState

    sealed class Effect : UiEffect

    sealed class Event : UiEvent {
        object LoadData : Event()
    }
}
