package presentation.screens.add_event

import extensions.currentHour
import extensions.nextHour
import extensions.nowInMillis
import presentation.base.BaseViewModel
import presentation.screens.add_event.AddEventContract.Effect
import presentation.screens.add_event.AddEventContract.Event
import presentation.screens.add_event.AddEventContract.State

class AddEventViewModel : BaseViewModel<Event, State, Effect>() {

    override fun createInitialState(): State {
        val state = State(
            startDate = nowInMillis,
            startHours = currentHour,
            endDate = nowInMillis,
            endHours = nextHour,
        )
        return state
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.ToggleAllDay -> setState { copy(allDay = event.isChecked) }
            else -> {}
        }
    }
}