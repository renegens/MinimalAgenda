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
        val startMinutes: Int,
        val durationInMinutes: String,
        val allDay: Boolean = false,
        val title: String = "",
        val location: String = "",
        val durationError: String? = null
    ) : UiState {

        fun isSaveEnabled() = title.isNotEmpty() && durationError == null || shouldOverride()

        private fun shouldOverride() = durationError != null && allDay
    }

    sealed class Effect : UiEffect {
        object Save : Effect()
        object Cancel : Effect()
    }

    sealed class Event : UiEvent {
        class ToggleAllDay(val isChecked: Boolean) : Event()
        class OnTitleEntered(val title: String) : Event()
        class OnLocationEntered(val location: String) : Event()
        class OnStartEntered(val timestamp: Long, val hours: Int, val minutes: Int) : Event()
        class OnDurationEntered(val duration: String) : Event()
        object OnSave : Event()
        object OnCancel : Event()
    }
}
