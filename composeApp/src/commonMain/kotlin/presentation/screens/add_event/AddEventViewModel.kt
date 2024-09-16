package presentation.screens.add_event

import androidx.lifecycle.viewModelScope
import domain.model.EventTime
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.add_event.AddEventContract.Effect
import presentation.screens.add_event.AddEventContract.Event
import presentation.screens.add_event.AddEventContract.State
import repository.CalendarRepository

class AddEventViewModel(
    private val repository: CalendarRepository
) :
    BaseViewModel<Event, State, Effect>() {

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.ToggleAllDay -> setState { copy(allDay = event.isAllDay) }

            is Event.OnCancel -> setEffect { Effect.Cancel }

            is Event.OnSave -> viewModelScope.launch {
                val eventTime = EventTime(
                    startTimestampInMillis = uiState.value.startDate,
                    startHours = uiState.value.startHours,
                    startMinutes = uiState.value.startMinutes,
                    durationInMinutes = uiState.value.durationInMinutes.toInt()
                )
                val didSave = repository.addEvent(
                    title = uiState.value.title,
                    location = uiState.value.location,
                    startTime = eventTime.getStarDateTime(),
                    endTime = eventTime.getEndDateTime(),
                    allDay = uiState.value.allDay
                )

                if (didSave) {
                    setEffect { Effect.Save }
                }
            }

            is Event.OnStartEntered -> {
                val error = validateDurationTime(
                    uiState.value.durationInMinutes,
                    event.hours,
                    event.minutes
                )
                if (error == null) {
                    setState {
                        copy(
                            durationError = null,
                            startMinutes = event.minutes,
                            startDate = event.timestamp,
                            startHours = event.hours
                        )
                    }
                } else {
                    setState {
                        copy(
                            durationError = error,
                            startMinutes = event.minutes,
                            startDate = event.timestamp,
                            startHours = event.hours
                        )
                    }
                }

            }

            is Event.OnDurationEntered -> {
                setState { copy(durationInMinutes = event.duration) }
                val error = validateDurationTime(
                    event.duration,
                    uiState.value.startHours,
                    uiState.value.startMinutes
                )
                if (error == null) {
                    setState { copy(durationError = null) }
                } else {
                    setState { copy(durationError = error) }
                }
            }

            is Event.OnTitleEntered -> setState { copy(title = event.title) }
            is Event.OnLocationEntered -> setState { copy(location = event.location) }
        }
    }

    private fun validateDurationTime(
        input: String,
        currentHours: Int,
        currentMinutes: Int
    ): String? {
        val time = input.toIntOrNull()
        return when {
            time == null || time == 0 -> "Please enter a valid number"
            time < 0 -> "We can't time travel yet. Time cannot be negative"
            time + currentMinutes + (currentHours * 60) > 1440 -> "Durations must be in the same day"
            else -> null // valid input
        }
    }
}