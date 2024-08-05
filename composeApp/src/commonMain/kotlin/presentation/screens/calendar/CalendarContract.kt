package presentation.screens.calendar

import data.Calendar
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState


class CalendarContract {

    data class State(
        val calendar: Calendar? = null
    ) : UiState

    sealed class Effect : UiEffect{
        object ScrollDayToToday: Effect()
        object ScrollMonthToToday: Effect()
        object ScrollYearToToday: Effect()
    }

    sealed class Event : UiEvent {
        object LoadData : Event()
        class OnNowClicked(val section: Section) : Event()
    }

    enum class Section(val position: Int) {
        Day(0), Month(1), Year(2);

        companion object {
            fun fromInt(value: Int) = entries.first { it.position == value }
        }
    }
}
