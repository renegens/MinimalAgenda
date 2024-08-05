package presentation.screens.calendar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.calendar.CalendarContract.*
import use_case.CalendarWithEventsUseCase

class CalendarViewModel(
    private val calendarWithEventsUseCase: CalendarWithEventsUseCase
) :
    BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.LoadData)
    }

    private fun loadData() {
        viewModelScope.launch {
            val data = calendarWithEventsUseCase()
            setState {
                State(calendar = data)
            }
        }
    }

    override fun createInitialState() = State(null)

    override fun handleEvent(event: Event) {
        when (event) {
            Event.LoadData -> loadData()
            is Event.OnNowClicked -> when (event.section) {
                Section.Day -> setEffect {
                    Effect.ScrollDayToToday
                }

                Section.Month -> setEffect {
                    Effect.ScrollMonthToToday
                }

                Section.Year -> setEffect {
                    Effect.ScrollYearToToday
                }
            }
        }
    }
}