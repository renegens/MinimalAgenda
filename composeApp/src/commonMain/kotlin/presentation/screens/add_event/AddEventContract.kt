package presentation.screens.add_event

import domain.model.AgendaEvent
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AddEventContract {

    data class State(
        val agendaEvent: AgendaEvent? = null,
        val startDate: Long,
        val startHours: Int,
        val endDate: Long,
        val endHours: Int,
        val allDay: Boolean = false,
        val title: String = ""
    ) : UiState

    sealed class Effect : UiEffect {

    }

    sealed class Event : UiEvent {
        class ToggleAllDay(val isChecked: Boolean) : Event()
    }
}
