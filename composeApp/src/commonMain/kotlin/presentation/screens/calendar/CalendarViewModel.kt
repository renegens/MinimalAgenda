package presentation.screens.calendar

import androidx.lifecycle.viewModelScope
import domain.use_case.CalendarGenerator
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.calendar.CalendarContract.*
import domain.use_case.GetAllEventsUseCase
import platform.CalendarPermissionHandler

class CalendarViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val permissionHandler: CalendarPermissionHandler,
    private val calendarGenerator: CalendarGenerator
) :
    BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.LoadData)
    }

    private fun loadData() {
        when (val permissionState = permissionHandler.getPermissionState()) {
            CalendarPermissionHandler.State.FullAccess -> viewModelScope.launch { loadEvents() }
            else ->
                setState {
                    State(
                        days = calendarGenerator.calendar,
                        calendarPermission = permissionState
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
            }

            //todo remove the index from here?
            is Event.OnDayClicked -> {
                setEffect {
                    Effect.GoToDay(index = getIndex(event.epochDay))
                }
            }

            is Event.OnMonthClicked -> {
                setEffect {
                    Effect.GoToMonth(event.agendaMonthYear)
                }
            }

            is Event.OnYearClicked -> {
                setEffect { Effect.GoToYear(event.agendaYear) }
            }

            Event.OnRequestCalendarPermissionClicked -> {
                viewModelScope.launch {
                    when (val state = permissionHandler.requestCalendarPermission()) {
                        CalendarPermissionHandler.State.FullAccess -> loadEvents()
                        else -> setState {
                            copy(
                                events = emptyList(),
                                calendarPermission = state
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadEvents() {
        when (val state = getAllEventsUseCase()) {
            is GetAllEventsUseCase.State.Success -> setState {
                State(
                    events = state.events,
                    days = calendarGenerator.calendar,
                    calendarPermission = CalendarPermissionHandler.State.FullAccess
                )
            }
        }
    }

    private fun getIndex(epochDay: Int): Int =
        uiState.value.days.indexOfFirst { it.epochDay == epochDay }
}