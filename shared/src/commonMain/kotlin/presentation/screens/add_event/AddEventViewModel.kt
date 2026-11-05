package presentation.screens.add_event

import androidx.lifecycle.viewModelScope
import data.calendar.CalendarMath
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import presentation.base.BaseViewModel
import presentation.screens.add_event.AddEventContract.Confirmation
import presentation.screens.add_event.AddEventContract.Effect
import presentation.screens.add_event.AddEventContract.Event
import presentation.screens.add_event.AddEventContract.State
import repository.CalendarRepository
import kotlin.time.Instant

class AddEventViewModel(
    private val repository: CalendarRepository,
    private val request: EventEditorRequest = EventEditorRequest.Add(),
) : BaseViewModel<Event, State, Effect>() {

    // `request` isn't assigned yet when the base class asks for the initial state, so it is applied in init.
    override fun createInitialState() = State()

    init {
        setState { initialEditorState(request) }
        if (request is EventEditorRequest.Add) loadCalendars()
    }

    private fun loadCalendars() {
        viewModelScope.launch {
            val writable = repository.getCalendars().filter { it.canWrite && it.id != null }
            val default = writable.firstOrNull { it.isPrimary } ?: writable.firstOrNull()
            setState { copy(calendars = writable, calendarId = default?.id) }
        }
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.ToggleAllDay -> setState { copy(allDay = event.isAllDay) }

            is Event.OnCancel -> setEffect { Effect.Cancel }

            is Event.OnSave -> {
                val state = uiState.value
                if (!state.isSaveEnabled()) return
                if (state.isEditing && state.isRecurring) {
                    setState { copy(confirmation = Confirmation.ChangeSeries) }
                } else {
                    save()
                }
            }

            is Event.OnDelete -> if (uiState.value.isEditing) {
                setState { copy(confirmation = Confirmation.DeleteEvent) }
            }

            is Event.OnConfirm -> {
                val confirmation = uiState.value.confirmation
                setState { copy(confirmation = null) }
                when (confirmation) {
                    Confirmation.DeleteEvent -> delete()
                    Confirmation.ChangeSeries -> save()
                    null -> {}
                }
            }

            is Event.OnDismissConfirmation -> setState { copy(confirmation = null) }

            is Event.OnStartEntered -> {
                val error = validateDurationTime(
                    uiState.value.durationInMinutes, event.hours, event.minutes, uiState.value.isEditing
                )
                setState {
                    copy(
                        durationError = error,
                        startMinutes = event.minutes,
                        startDate = event.timestamp,
                        startHours = event.hours
                    )
                }
            }

            is Event.OnDurationEntered -> {
                val error = validateDurationTime(
                    event.duration, uiState.value.startHours, uiState.value.startMinutes, uiState.value.isEditing
                )
                setState { copy(durationInMinutes = event.duration, durationError = error) }
            }

            is Event.OnTitleEntered -> setState { copy(title = event.title) }
            is Event.OnLocationEntered -> setState { copy(location = event.location) }
            is Event.OnCalendarSelected -> setState { copy(calendarId = event.calendarId) }
        }
    }

    private fun save() {
        val state = uiState.value
        setState { copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            val (start, end) = state.toStartAndEnd()
            val id = state.editingEventId
            val didSave = if (id == null) {
                repository.addEvent(
                    title = state.title.trim(),
                    location = state.location.trim(),
                    startTime = start,
                    endTime = end,
                    allDay = state.allDay,
                    calendarId = state.calendarId,
                )
            } else {
                repository.updateEvent(
                    id = id,
                    title = state.title.trim(),
                    location = state.location.trim(),
                    startTime = start,
                    endTime = end,
                    allDay = state.allDay,
                )
            }
            finish(didSave)
        }
    }

    private fun delete() {
        val id = uiState.value.editingEventId ?: return
        setState { copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch { finish(repository.deleteEvent(id)) }
    }

    private fun finish(success: Boolean) {
        if (success) {
            setEffect { Effect.Save }
        } else {
            setState { copy(isSaving = false, saveFailed = true) }
        }
    }

    private fun validateDurationTime(
        input: String,
        currentHours: Int,
        currentMinutes: Int,
        isEditing: Boolean,
    ): String? {
        val time = input.toIntOrNull()
        return when {
            time == null || (time == 0 && !isEditing) -> "Please enter a valid number"
            time < 0 -> "We can't time travel yet. Time cannot be negative"
            // The editor can't express multi-day timed events, but it must not block editing one.
            !isEditing && time + currentMinutes + (currentHours * 60) > 1440 -> "Durations must be in the same day"
            else -> null // valid input
        }
    }
}

/**
 * Local start and end for the repository. Timed: start + duration on the real timeline (so it may cross
 * midnight when editing). All-day: first day at 00:00 and the last day (inclusive) at 23:59.
 */
internal fun State.toStartAndEnd(zone: TimeZone = TimeZone.currentSystemDefault()): Pair<LocalDateTime, LocalDateTime> {
    val date = Instant.fromEpochMilliseconds(startDate).toLocalDateTime(TimeZone.UTC).date
    if (allDay) {
        val last = date.plus(allDayDays - 1, DateTimeUnit.DAY)
        return LocalDateTime(date, LocalTime(0, 0)) to LocalDateTime(last, LocalTime(23, 59))
    }
    val start = LocalDateTime(date, LocalTime(startHours, startMinutes))
    val minutes = durationInMinutes.toLongOrNull() ?: 0L
    return start to CalendarMath.endAfter(start, minutes, zone)
}
