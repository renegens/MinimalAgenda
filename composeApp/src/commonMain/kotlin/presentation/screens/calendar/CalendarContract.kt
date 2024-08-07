package presentation.screens.calendar

import domain.model.AgendaDay
import domain.model.AgendaMonthYear
import extensions.todayEpochDay
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState


class CalendarContract {

    data class State(
        val days: List<AgendaDay> = emptyList()
    ) : UiState

    sealed class Effect : UiEffect {
        object ScrollDayToToday : Effect()
        object ScrollMonthToToday : Effect()
        object ScrollYearToToday : Effect()
        class GoToDay(val index: Int) : Effect()
        class GoToMonth(val epochDay: Int) : Effect()
    }

    sealed class Event : UiEvent {
        object LoadData : Event()
        class OnNowClicked(val section: Section) : Event()
        class OnDayClicked(val epochDay: Int) : Event()
        class OnMonthClicked(val agendaMonthYear: AgendaMonthYear) : Event()
    }

    enum class Section(val position: Int) {
        Day(0), Month(1), Year(2);

        companion object {
            fun fromInt(value: Int) = entries.first { it.position == value }
        }
    }
}
