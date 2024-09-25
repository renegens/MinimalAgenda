package presentation.screens.calendar

import androidx.lifecycle.viewModelScope
import domain.model.AgendaEvent
import domain.use_case.CalendarGenerator
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.calendar.CalendarContract.*
import domain.use_case.GetAllEventsUseCase
import domain.use_case.ScheduleNotificationUseCase
import domain.use_case.ToggleEventVisibilityUseCase
import domain.model.DayListViewType
import extensions.guardPremium
import platform.CalendarPermissionHandler

class CalendarViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val permissionHandler: CalendarPermissionHandler,
    private val calendarGenerator: CalendarGenerator,
    private val toggleEventVisibilityUseCase: ToggleEventVisibilityUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
) : BaseViewModel<Event, State, Effect>() {

    private fun loadData() {
        when (val permissionState = permissionHandler.getPermissionState()) {
            CalendarPermissionHandler.State.FullAccess -> viewModelScope.launch {
                loadEvents()
            }

            else -> setState {
                State(
                    days = calendarGenerator.calendar, calendarPermission = permissionState
                )
            }
        }
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.LoadData -> loadData()
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

                Section.Settings -> {}
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
                    CalendarPermissionHandler.State.FullAccess -> loadEvents()
                    else -> setState {
                        copy(
                            agendaEvents = emptyList(), calendarPermission = state
                        )
                    }
                }
            }

            is Event.ToggleEventVisibility -> guardPremium(onSubscribed = {
                toggleEventVisibility(
                    event.agendaEvent
                )
            },
                onPaywall = { setEffect { Effect.ShowPaywall } })

            is Event.Notify -> guardPremium(onSubscribed = { notifyEvent(event.agendaEvent) },
                onPaywall = { setEffect { Effect.ShowPaywall } })

            Event.ToggleActive -> setState { copy(showAll = !uiState.value.showAll) }
            is Event.OnEditEvent -> guardPremium(onSubscribed = { setEffect { Effect.ShowEdit(event.agendaEvent) } },
                onPaywall = { setEffect { Effect.ShowPaywall } })
        }
    }

    private suspend fun loadEvents() {
        when (val state = getAllEventsUseCase()) {
            is GetAllEventsUseCase.State.Success -> setState {
                State(
                    flatDaysAndEvents = state.dayListViewTypes,
                    agendaEvents = state.agendaEvents,
                    days = calendarGenerator.calendar,
                    calendarPermission = CalendarPermissionHandler.State.FullAccess
                )
            }
        }
    }

    private fun toggleEventVisibility(agendaEvent: AgendaEvent) {
        viewModelScope.launch {
            when (val state = toggleEventVisibilityUseCase(agendaEvent = agendaEvent)) {
                is ToggleEventVisibilityUseCase.State.Success -> {
                    val events = uiState.value.flatDaysAndEvents.toMutableList()
                    val index = events.indexOfFirst { it is DayListViewType.Event && it.event == agendaEvent }
                    events.removeAt(index)
                    events.add(index, DayListViewType.Event(state.agendaEvent))

                    setState { copy(flatDaysAndEvents = events) }
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
}