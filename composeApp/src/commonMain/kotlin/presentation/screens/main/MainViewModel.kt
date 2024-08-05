package presentation.screens.main

import androidx.lifecycle.viewModelScope
import data.CalendarGenerator
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.screens.main.MainContract.*
import repository.CalendarRepository
import use_case.CalendarWithEvents


class MainViewModel(
    private val calendarRepository: CalendarRepository,
    private val calendarGenerator: CalendarGenerator,
    private val calendarWithEvents: CalendarWithEvents
) :
    BaseViewModel<Event, State, Effect>() {

    init {
        setEvent(Event.LoadData)
    }

    private fun loadData() {
        viewModelScope.launch {
            val data = calendarWithEvents()
            setState {
                State(calendar = data)
            }
        }
    }

    override fun createInitialState() = State(null)

    override fun handleEvent(event: Event) {
        when (event) {
            Event.LoadData -> loadData()
        }
    }
}