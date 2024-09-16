package presentation.screens.add_event

import extensions.nextHour
import extensions.nowInMillis
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AddEventContract {
    data class State(
        val startDate: Long = nowInMillis,
        val startHours: Int = nextHour,
        val startMinutes: Int = 0,
        val durationInMinutes: String = "60",
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
        class ToggleAllDay(val isAllDay: Boolean) : Event()
        class OnTitleEntered(val title: String) : Event()
        class OnLocationEntered(val location: String) : Event()
        class OnStartEntered(val timestamp: Long, val hours: Int, val minutes: Int) : Event()
        class OnDurationEntered(val duration: String) : Event()
        object OnSave : Event()
        object OnCancel : Event()
    }
}
