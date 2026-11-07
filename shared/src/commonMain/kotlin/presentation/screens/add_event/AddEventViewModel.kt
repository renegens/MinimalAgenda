package presentation.screens.add_event

import androidx.lifecycle.viewModelScope
import data.calendar.EventTimeRange
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
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
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
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
        if (uiState.value.isSaving) return
        when (event) {
            is Event.ToggleAllDay -> setState {
                val end = toStartAndEnd(timeZone).second
                val days = pickerDate(startDate).daysUntil(end.date) + 1
                copy(allDay = event.isAllDay, allDayDays = if (event.isAllDay) days else allDayDays)
            }

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

            is Event.OnStartEntered -> setState {
                copy(
                    startInstantOverride = if (event.timestamp == startDate && event.hours == startHours && event.minutes == startMinutes)
                        startInstantOverride else null,
                    startDate = event.timestamp,
                    startHours = event.hours,
                    startMinutes = event.minutes,
                    startSeconds = if (event.hours == startHours && event.minutes == startMinutes) startSeconds else 0,
                )
            }

            is Event.OnEndEntered -> setState {
                val start = LocalDateTime(pickerDate(startDate), LocalTime(startHours, startMinutes, startSeconds))
                val range = timedRange(timeZone)
                val previousEnd = range.end.toLocalDateTime(timeZone)
                val date = pickerDate(event.timestamp)
                val displayedEnd = toStartAndEnd(timeZone).second
                if (date == displayedEnd.date && (allDay ||
                        event.hours == displayedEnd.hour && event.minutes == displayedEnd.minute)) return@setState this
                val seconds = if (event.hours == previousEnd.hour && event.minutes == previousEnd.minute) previousEnd.second else 0
                val endTime = if (allDay) previousEnd.time else LocalTime(event.hours, event.minutes, seconds)
                val end = LocalDateTime(date, endTime)
                copy(
                    durationInSeconds = (end.toInstant(timeZone) - range.start).inWholeSeconds,
                    allDayDays = start.date.daysUntil(date) + 1,
                )
            }

            is Event.OnDurationSelected -> if (event.minutes in durationQuickPicks) {
                setState { copy(durationInSeconds = event.minutes * 60L) }
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
            val (start, end) = state.toStartAndEnd(timeZone)
            val id = state.editingEventId
            val didSave = writeResult {
                if (id == null) {
                    repository.addEvent(
                        title = state.title.trim(),
                        location = state.location.trim(),
                        startTime = start,
                        endTime = end,
                        allDay = state.allDay,
                        calendarId = state.calendarId,
                        timedRange = if (state.allDay) null else state.timedRange(timeZone),
                    )
                } else {
                    repository.updateEvent(
                        id = id,
                        title = state.title.trim(),
                        location = state.location.trim(),
                        startTime = start,
                        endTime = end,
                        allDay = state.allDay,
                        timedRange = if (state.allDay) null else state.timedRange(timeZone),
                    )
                }
            }
            finish(didSave)
        }
    }

    private fun delete() {
        val id = uiState.value.editingEventId ?: return
        setState { copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch { finish(writeResult { repository.deleteEvent(id) }) }
    }

    private suspend fun writeResult(block: suspend () -> Boolean): Boolean = try {
        block()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        false
    }

    private fun finish(success: Boolean) {
        if (success) {
            setEffect { Effect.Save }
        } else {
            setState { copy(isSaving = false, saveFailed = true) }
        }
    }
}

/**
 * Local start and end for the repository. Timed: start + elapsed duration on the real timeline.
 * All-day: first day at 00:00 and the last day (inclusive) at 23:59:59.
 */
internal fun State.toStartAndEnd(zone: TimeZone = TimeZone.currentSystemDefault()): Pair<LocalDateTime, LocalDateTime> {
    val date = pickerDate(startDate)
    if (allDay) {
        val last = date.plus(allDayDays - 1, DateTimeUnit.DAY)
        return LocalDateTime(date, LocalTime(0, 0)) to LocalDateTime(last, LocalTime(23, 59, 59))
    }
    val start = LocalDateTime(date, LocalTime(startHours, startMinutes, startSeconds))
    return start to timedRange(zone).end.toLocalDateTime(zone)
}

internal fun pickerDate(timestamp: Long) =
    Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.UTC).date

internal fun State.timedRange(zone: TimeZone): EventTimeRange {
    val start = startInstantOverride ?: LocalDateTime(
        pickerDate(startDate), LocalTime(startHours, startMinutes, startSeconds)
    ).toInstant(zone)
    return EventTimeRange(start, start + durationInSeconds.seconds)
}
