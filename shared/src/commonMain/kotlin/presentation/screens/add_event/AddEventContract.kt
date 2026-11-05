package presentation.screens.add_event

import data.calendar.CalendarEntity
import domain.model.AgendaEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import extensions.nextHour
import extensions.today
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

/** Picker convention (Material and Cupertino date pickers): UTC midnight of the selected calendar date. */
fun LocalDate.toPickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

class AddEventContract {
    enum class Confirmation { DeleteEvent, ChangeSeries }

    data class State(
        /** UTC midnight (ms) of the selected date, as the date pickers report it. */
        val startDate: Long = today.toPickerMillis(),
        val startHours: Int = nextHour,
        val startMinutes: Int = 0,
        val durationInMinutes: String = "60",
        val allDay: Boolean = false,
        val title: String = "",
        val location: String = "",
        val durationError: String? = null,
        /** Non-null in edit mode: the id of the event being edited. */
        val editingEventId: String? = null,
        val isRecurring: Boolean = false,
        /** Number of days an all-day event spans (no UI to change it; kept so editing never shrinks it). */
        val allDayDays: Int = 1,
        /** Writable calendars (add mode only). The picker is shown when there is more than one. */
        val calendars: List<CalendarEntity> = emptyList(),
        val calendarId: String? = null,
        val confirmation: Confirmation? = null,
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
    ) : UiState {

        val isEditing get() = editingEventId != null

        /** Recurring series are edited as a whole, so only title and location can be changed. */
        val timesEditable get() = !(isEditing && isRecurring)

        fun isSaveEnabled() =
            !isSaving && title.isNotBlank() && (!timesEditable || durationError == null || allDay)
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
        class OnCalendarSelected(val calendarId: String) : Event()
        object OnSave : Event()
        object OnCancel : Event()
        object OnDelete : Event()
        object OnConfirm : Event()
        object OnDismissConfirmation : Event()
    }
}

/** Initial editor state for [request]; for an edit it is prefilled from the event. */
fun initialEditorState(request: EventEditorRequest): AddEventContract.State = when (request) {
    EventEditorRequest.Add -> AddEventContract.State()
    is EventEditorRequest.Edit -> request.event.toEditorState()
}

internal fun AgendaEvent.toEditorState(): AddEventContract.State {
    val startDate = startLocalDateTime.date
    val lastDate = if (endLocalDateTime.date > startDate) endLocalDateTime.date else startDate
    val minutes = ((endTimeInUTC ?: startTimeInUTC) - startTimeInUTC) / 60
    return AddEventContract.State(
        startDate = startDate.toPickerMillis(),
        startHours = if (allDay) 0 else startLocalDateTime.hour,
        startMinutes = if (allDay) 0 else startLocalDateTime.minute,
        durationInMinutes = if (allDay) "60" else minutes.coerceAtLeast(0).toString(),
        allDay = allDay,
        title = title,
        location = location.orEmpty(),
        editingEventId = id,
        isRecurring = isRecurring,
        allDayDays = startDate.daysUntil(lastDate) + 1,
    )
}
