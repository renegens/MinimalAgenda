package presentation.screens.add_event

import domain.model.AgendaEvent
import domain.model.EventTime
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AddEventContract {

    data class State(
        val agendaEvent: AgendaEvent? = null,
        val startDate: Long,
        val startHours: Int,
        val startMinutes: Int,
        val durationInMinutes: Int,
        val allDay: Boolean = false,
        val title: String = "",
        val durationError: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object OnSave: Effect()
    }

    sealed class Event : UiEvent {
        class ToggleAllDay(val isChecked: Boolean) : Event()
        class OnTitleEntered(val title: String): Event()
        class OnStartEntered(val timestamp: Long, val hours: Int, val minutes: Int): Event()
        class OnDurationEntered(val duration: String): Event()
        object OnSave: Event()
    }
}
