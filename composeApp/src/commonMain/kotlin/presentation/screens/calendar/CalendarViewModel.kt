package presentation.screens.calendar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.calendar.CalendarContract.*
import domain.use_case.CalendarWithEventsUseCase
import extensions.today
import extensions.todayEpochDay

class CalendarViewModel(
    private val calendarWithEventsUseCase: CalendarWithEventsUseCase
) :
    BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.LoadData)
    }

    private fun loadData() {
        viewModelScope.launch {
            val agendaDays = calendarWithEventsUseCase()
            setState {
                State(days = agendaDays)
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
        }
    }

    private fun getIndex(epochDay: Int): Int =
        uiState.value.days.indexOfFirst { it.epochDay == epochDay }

}