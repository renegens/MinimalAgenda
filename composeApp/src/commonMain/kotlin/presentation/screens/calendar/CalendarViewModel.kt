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
import domain.use_case.FlatCalendarViewFactory
import domain.use_case.ShouldPresentSwipeToolTipUseCase
import extensions.guardPremium
import extensions.today
import platform.CalendarPermissionHandler

class CalendarViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val permissionHandler: CalendarPermissionHandler,
    private val calendarGenerator: CalendarGenerator,
    private val toggleEventVisibilityUseCase: ToggleEventVisibilityUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val flatCalendarViewFactory: FlatCalendarViewFactory,
    private val shouldPresentSwipeToolTipUseCase: ShouldPresentSwipeToolTipUseCase
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
                    CalendarPermissionHandler.State.FullAccess -> loadEventsFastAndSlow(scrollToNow = true)
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

            Event.HideSwipeToolTip -> setState { copy(showSwipeToolTip = false) }
        }
    }

    private fun loadData() {
        when (val permissionState = permissionHandler.getPermissionState()) {
            CalendarPermissionHandler.State.FullAccess -> viewModelScope.launch {
                val eventsUIList = uiState.value.flatDaysAndEvents

                if (eventsUIList.isEmpty()) {
                    loadEventsFastAndSlow(scrollToNow = true)
                    return@launch
                }

                when (val slowIteration = getAllEventsUseCase(24)) {
                    is GetAllEventsUseCase.State.Success -> {
                        setState {
                            copy(
                                currentDate = today,
                                flatDaysAndEvents = slowIteration.dayListViewTypes,
                                agendaEvents = slowIteration.agendaEvents,
                                scrollToNow = eventsUIList.size != slowIteration.dayListViewTypes.size
                            )
                        }
                    }
                }
            }

            else -> viewModelScope.launch {
                val shouldPresentSwipeTooltip = shouldPresentSwipeToolTipUseCase()
                setState {
                    State(
                        days = calendarGenerator.calendar,
                        flatDaysAndEvents = flatCalendarViewFactory.build(),
                        showSwipeToolTip = shouldPresentSwipeTooltip,
                        calendarPermission = permissionState
                    )
                }
            }
        }
    }

    private fun reloadData() {
        when (val permissionState = permissionHandler.getPermissionState()) {
            CalendarPermissionHandler.State.FullAccess -> viewModelScope.launch {
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

            else -> setState {
                State(
                    days = calendarGenerator.calendar,
                    flatDaysAndEvents = flatCalendarViewFactory.build(),
                    calendarPermission = permissionState
                )
            }
        }
    }

    private suspend fun loadEventsFastAndSlow(scrollToNow: Boolean) {
        when (val fastIteration = getAllEventsUseCase(1)) {
            is GetAllEventsUseCase.State.Success -> {
                setState {
                    copy(
                        currentDate = today,
                        days = calendarGenerator.calendar,
                        flatDaysAndEvents = fastIteration.dayListViewTypes,
                        agendaEvents = fastIteration.agendaEvents,
                        calendarPermission = CalendarPermissionHandler.State.FullAccess,
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
                    val events = uiState.value.flatDaysAndEvents.toMutableList()
                    val index =
                        events.indexOfFirst { it is DayListViewType.Event && it.event == agendaEvent }
                    events.removeAt(index)
                    events.add(index, DayListViewType.Event(state.agendaEvent))
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
}