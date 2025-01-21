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
import domain.use_case.DidSeeSwipeTooltipUseCase
import extensions.guardPremium
import extensions.isPremium
import extensions.today
import platform.CalendarPermissionHandler

class CalendarViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val permissionHandler: CalendarPermissionHandler,
    private val calendarGenerator: CalendarGenerator,
    private val toggleEventVisibilityUseCase: ToggleEventVisibilityUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val didSeeSwipeTooltipUseCase: DidSeeSwipeTooltipUseCase
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
                val didSeeSwipeTooltip = didSeeSwipeTooltipUseCase()

                when (val state = permissionHandler.requestCalendarPermission()) {
                    CalendarPermissionHandler.State.FullAccess -> loadEventsFastAndSlow(
                        scrollToNow = true, shouldShowSwipeToolTip = didSeeSwipeTooltip.not()
                    )

                    else -> setState {
                        copy(
                            agendaEvents = emptyList(), calendarPermission = state
                        )
                    }
                }
            }

            is Event.ToggleEventVisibility -> guardPremiumLetThroughDemo(onSubscribed = {
                toggleEventVisibility(
                    event.agendaEvent
                )
            }, onPaywall = { setEffect { Effect.ShowPaywall } })

            is Event.Notify -> {
                guardPremiumLetThroughDemo(onSubscribed = { notifyEvent(event.agendaEvent) },
                    onPaywall = { setEffect { Effect.ShowPaywall } })
            }

            Event.ToggleActive -> setState { copy(showAll = !uiState.value.showAll) }
            is Event.OnEditEvent -> guardPremium(onSubscribed = { setEffect { Effect.ShowEdit(event.agendaEvent) } },
                onPaywall = { setEffect { Effect.ShowPaywall } })

            Event.HideSwipeToolTip -> setState { copy(showSwipeToolTip = false) }
            Event.DismissBottomSheet -> setEffect { Effect.DismissSheet }
            Event.ShowPaywallFromBottomSheet -> {
                setEffect { Effect.DismissSheet }
                setEffect { Effect.ShowPaywall }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val eventsUIList = uiState.value.flatDaysAndEvents
            val didSeeSwipeTooltip = didSeeSwipeTooltipUseCase()
            val calendarPermissionState = permissionHandler.getPermissionState()

            if (eventsUIList.isEmpty()) {
                loadEventsFastAndSlow(
                    scrollToNow = true, shouldShowSwipeToolTip = didSeeSwipeTooltip.not()
                )
                return@launch
            }

            when (val slowIteration = getAllEventsUseCase(24)) {
                is GetAllEventsUseCase.State.Success -> {
                    setState {
                        copy(
                            currentDate = today,
                            calendarPermission = calendarPermissionState,
                            showSwipeToolTip = didSeeSwipeTooltip.not(),
                            flatDaysAndEvents = slowIteration.dayListViewTypes,
                            agendaEvents = slowIteration.agendaEvents,
                            isDemoMode = calendarPermissionState != CalendarPermissionHandler.State.FullAccess,
                            scrollToNow = eventsUIList.size != slowIteration.dayListViewTypes.size
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

    private suspend fun loadEventsFastAndSlow(
        scrollToNow: Boolean, shouldShowSwipeToolTip: Boolean
    ) {
        val calendarPermissionState = permissionHandler.getPermissionState()

        when (val fastIteration = getAllEventsUseCase(1)) {
            is GetAllEventsUseCase.State.Success -> {
                setState {
                    copy(
                        currentDate = today,
                        days = calendarGenerator.calendar,
                        flatDaysAndEvents = fastIteration.dayListViewTypes,
                        agendaEvents = fastIteration.agendaEvents,
                        isDemoMode = calendarPermissionState != CalendarPermissionHandler.State.FullAccess,
                        calendarPermission = calendarPermissionState,
                        scrollToNow = scrollToNow,
                        showSwipeToolTip = shouldShowSwipeToolTip
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
        //todo move to use case
        val isPremium = isPremium()
        if (calendarPermissionState == CalendarPermissionHandler.State.FullAccess && isPremium.not()) {
            setEffect { Effect.ShowSheetTip }
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

    private fun guardPremiumLetThroughDemo(onSubscribed: () -> Unit, onPaywall: () -> Unit) {
        if (permissionHandler.getPermissionState() == CalendarPermissionHandler.State.FullAccess) {
            guardPremium(onSubscribed = onSubscribed, onPaywall = onPaywall)
        } else {
            onSubscribed.invoke()
        }
    }
}