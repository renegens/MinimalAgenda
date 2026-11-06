package presentation.screens.add_event

import data.calendar.CalendarEntity
import domain.model.AgendaEvent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import extensions.nextHour
import extensions.today
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

/** Picker convention: UTC midnight of the selected calendar date. */
fun LocalDate.toPickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

internal val durationQuickPicks = listOf(10, 15, 30, 60, 90, 120)

class AddEventContract {
    enum class Confirmation { DeleteEvent, ChangeSeries }

    data class State(
        /** UTC midnight (ms) of the selected date, as the date pickers report it. */
        val startDate: Long = today.toPickerMillis(),
        val startHours: Int = nextHour,
        val startMinutes: Int = 0,
        val startSeconds: Int = 0,
        /** Preserves an existing start in a repeated DST hour until the user changes it. */
        val startInstantOverride: Instant? = null,
        /** Elapsed duration on the real timeline; end controls and chips edit the same value. */
        val durationInSeconds: Long = 3_600,
        val allDay: Boolean = false,
        val title: String = "",
        val location: String = "",
        /** Non-null in edit mode: the id of the event being edited. */
        val editingEventId: String? = null,
        val isRecurring: Boolean = false,
        /** Number of calendar days covered by an all-day event; the Ends date is inclusive. */
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

        val timeError: String?
            get() = when {
                allDay && allDayDays < 1 -> "End date must be on or after the start date"
                !allDay && durationInSeconds <= 0 -> "End time must be after the start time"
                else -> null
            }

        val selectedDurationMinutes: Int?
            get() = durationQuickPicks.firstOrNull { durationInSeconds == it * 60L }

        fun isSaveEnabled() =
            !isSaving && title.isNotBlank() && (!timesEditable || timeError == null)
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
        class OnEndEntered(val timestamp: Long, val hours: Int, val minutes: Int) : Event()
        class OnDurationSelected(val minutes: Int) : Event()
        class OnCalendarSelected(val calendarId: String) : Event()
        object OnSave : Event()
        object OnCancel : Event()
        object OnDelete : Event()
        object OnConfirm : Event()
        object OnDismissConfirmation : Event()
    }
}

/** Initial editor state for [request]; for an edit it is prefilled from the event. */
fun initialEditorState(
    request: EventEditorRequest,
    now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
): AddEventContract.State = when (request) {
    is EventEditorRequest.Add -> {
        val requestedDate = request.date ?: now.date
        val isToday = requestedDate == now.date
        val rollsToTomorrow = isToday && now.hour == 23
        val date = if (rollsToTomorrow) requestedDate.plus(1, DateTimeUnit.DAY) else requestedDate
        AddEventContract.State(
            startDate = date.toPickerMillis(),
            startHours = if (isToday) (now.hour + 1) % 24 else 9,
            startMinutes = 0,
        )
    }
    is EventEditorRequest.Edit -> request.event.toEditorState()
}

internal fun AgendaEvent.toEditorState(): AddEventContract.State {
    val startDate = startLocalDateTime.date
    val lastDate = if (endLocalDateTime.date > startDate) endLocalDateTime.date else startDate
    val duration = (endTimeInUTC ?: startTimeInUTC) - startTimeInUTC
    return AddEventContract.State(
        startDate = startDate.toPickerMillis(),
        startHours = if (allDay) 0 else startLocalDateTime.hour,
        startMinutes = if (allDay) 0 else startLocalDateTime.minute,
        startSeconds = if (allDay) 0 else startLocalDateTime.second,
        startInstantOverride = if (allDay) null else Instant.fromEpochSeconds(startTimeInUTC),
        durationInSeconds = if (allDay) 3_600 else duration,
        allDay = allDay,
        title = title,
        location = location.orEmpty(),
        editingEventId = id,
        isRecurring = isRecurring,
        allDayDays = startDate.daysUntil(lastDate) + 1,
    )
}
