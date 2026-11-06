package presentation.screens.calendar

import androidx.lifecycle.viewModelScope
import domain.model.AgendaEvent
import domain.use_case.CalendarGenerator
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.calendar.CalendarContract.*
import domain.use_case.CreateLocalCalendarUseCase
import domain.use_case.GetAllEventsUseCase
import domain.use_case.HasCalendarsUseCase
import domain.use_case.ScheduleNotificationUseCase
import domain.use_case.ToggleEventVisibilityUseCase
import domain.model.DayListViewType
import extensions.today
import platform.CalendarPermissionHandler

class CalendarViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val permissionHandler: CalendarPermissionHandler,
    private val calendarGenerator: CalendarGenerator,
    private val toggleEventVisibilityUseCase: ToggleEventVisibilityUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val hasCalendarsUseCase: HasCalendarsUseCase,
    private val createLocalCalendarUseCase: CreateLocalCalendarUseCase,
) : BaseViewModel<Event, State, Effect>() {

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.InitialDataLoad -> loadData()
            Event.ForceReload -> reloadData()
            is Event.OnNowClicked -> when (event.section) {
                Section.Day -> {
                    setEffect {
                        Effect.ScrollDayToToday
                    }
                }

                Section.Month -> setEffect {
                    Effect.ScrollMonthToToday
                }

                Section.Year -> setEffect {
                    Effect.ScrollYearToToday
                }
            }

            is Event.OnDayClicked -> setEffect {
                Effect.GoToDay(epochDay = event.epochDay)
            }

            is Event.OnMonthClicked -> setEffect {
                Effect.GoToMonth(event.agendaMonthYear)
            }

            is Event.OnYearClicked -> setEffect { Effect.GoToYear(event.agendaYear) }

            Event.OnRequestCalendarPermissionClicked -> viewModelScope.launch {

                when (val state = permissionHandler.requestCalendarPermission()) {
                    CalendarPermissionHandler.State.FullAccess -> loadEventsFastAndSlow(scrollToNow = true)

                    else -> setState {
                        copy(
                            agendaEvents = emptyList(), calendarPermission = state
                        )
                    }
                }
            }

            Event.OnCreateLocalCalendarClicked -> viewModelScope.launch {
                if (createLocalCalendarUseCase()) loadEventsFastAndSlow(scrollToNow = true)
            }

            is Event.ToggleEventVisibility -> toggleEventVisibility(event.agendaEvent)

            is Event.Notify -> notifyEvent(event.agendaEvent)

            Event.ToggleActive -> setState { copy(showAll = !uiState.value.showAll) }
            is Event.OnEditEvent -> setEffect { Effect.ShowEdit(event.agendaEvent) }
            Event.ShowSettings -> setEffect { Effect.ShowSettings }
            Event.RefreshPermissions -> refreshPermissions()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val eventsUIList = uiState.value.flatDaysAndEvents
            val calendarPermissionState = permissionHandler.getPermissionState()
            val noCalendars = calendarPermissionState == CalendarPermissionHandler.State.FullAccess && !hasCalendarsUseCase()

            if (eventsUIList.isEmpty()) {
                loadEventsFastAndSlow(
                    scrollToNow = true
                )
                return@launch
            }

            when (val slowIteration = getAllEventsUseCase(24)) {
                is GetAllEventsUseCase.State.Success -> {
                    setState {
                        copy(
                            currentDate = today,
                            calendarPermission = calendarPermissionState,
                            flatDaysAndEvents = slowIteration.dayListViewTypes,
                            agendaEvents = slowIteration.agendaEvents,
                            isDemoMode = calendarPermissionState != CalendarPermissionHandler.State.FullAccess,
                            noCalendars = noCalendars,
                            scrollToNow = eventsUIList.size != slowIteration.dayListViewTypes.size,
                        )
                    }
                }
            }
        }
    }

    private fun reloadData() {
        viewModelScope.launch {
            when (val slowIteration = getAllEventsUseCase(24)) {
                is GetAllEventsUseCase.State.Success -> {
                    setState {
                        copy(
                            flatDaysAndEvents = slowIteration.dayListViewTypes,
                            agendaEvents = slowIteration.agendaEvents,
                            scrollToNow = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadEventsFastAndSlow(scrollToNow: Boolean) {
        val calendarPermissionState = permissionHandler.getPermissionState()
        val noCalendars = calendarPermissionState == CalendarPermissionHandler.State.FullAccess && !hasCalendarsUseCase()

        when (val fastIteration = getAllEventsUseCase(1)) {
            is GetAllEventsUseCase.State.Success -> {
                setState {
                    copy(
                        currentDate = today,
                        days = calendarGenerator.calendar,
                        flatDaysAndEvents = fastIteration.dayListViewTypes,
                        agendaEvents = fastIteration.agendaEvents,
                        isDemoMode = calendarPermissionState != CalendarPermissionHandler.State.FullAccess,
                        noCalendars = noCalendars,
                        calendarPermission = calendarPermissionState,
                        scrollToNow = scrollToNow,
                    )
                }
                when (val slowIteration = getAllEventsUseCase(24)) {
                    is GetAllEventsUseCase.State.Success -> {
                        setState {
                            copy(
                                flatDaysAndEvents = slowIteration.dayListViewTypes,
                                agendaEvents = slowIteration.agendaEvents,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toggleEventVisibility(agendaEvent: AgendaEvent) {
        viewModelScope.launch {
            when (val state = toggleEventVisibilityUseCase(agendaEvent = agendaEvent)) {
                is ToggleEventVisibilityUseCase.State.Success -> {
                    val events = replaceEvent(
                        uiState.value.flatDaysAndEvents, agendaEvent, state.agendaEvent
                    )
                    setState { copy(flatDaysAndEvents = events, scrollToNow = false) }
                }
            }
        }
    }

    private fun notifyEvent(agendaEvent: AgendaEvent) {
        viewModelScope.launch {
            when (scheduleNotificationUseCase(agendaEvent = agendaEvent)) {
                ScheduleNotificationUseCase.State.Fail -> {}
                ScheduleNotificationUseCase.State.PermissionMissing -> setEffect {
                    Effect.NotificationPermissionMissing
                }

                ScheduleNotificationUseCase.State.Success -> {}
            }
        }
    }

    private fun refreshPermissions() {
        viewModelScope.launch {
            val previousPermissionState = uiState.value.calendarPermission
            val currentPermissionState = permissionHandler.getPermissionState()

            // If permission changed from denied/not determined to full access, reload events
            if (previousPermissionState != CalendarPermissionHandler.State.FullAccess 
                && currentPermissionState == CalendarPermissionHandler.State.FullAccess) {
                loadEventsFastAndSlow(scrollToNow = true)
            } else {
                // Update permission state even if it didn't change to full access
                setState { copy(calendarPermission = currentPermissionState) }
            }
        }
    }
}

/** Returns [items] with the entry for [old] replaced by [new]; unchanged if [old] isn't in the list. */
internal fun replaceEvent(
    items: List<DayListViewType>,
    old: AgendaEvent,
    new: AgendaEvent
): List<DayListViewType> {
    val index = items.indexOfFirst { it is DayListViewType.Event && it.event == old }
    if (index < 0) return items
    return items.toMutableList().also { it[index] = DayListViewType.Event(new) }
}
