package presentation.screens.calendar

import data.local.HiddenEventEntity
import domain.model.AgendaDay
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import platform.CalendarPermissionHandler
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class CalendarContract {

    data class State(
        val days: List<AgendaDay> = emptyList(),
        val agendaEvents: List<AgendaEvent> = emptyList(),
        val showAll: Boolean = false,
        val calendarPermission: CalendarPermissionHandler.State = CalendarPermissionHandler.State.NotDetermined
    ) : UiState

    sealed class Effect : UiEffect {
        object ScrollDayToToday : Effect()
        object ScrollMonthToToday : Effect()
        object ScrollYearToToday : Effect()
        class GoToDay(val index: Int) : Effect()
        class GoToMonth(val agendaMonthYear: AgendaMonthYear) : Effect()
        class GoToYear(val agendaYear: AgendaYear) : Effect()
    }

    sealed class Event : UiEvent {
        object LoadData : Event()
        class OnNowClicked(val section: Section) : Event()
        class OnDayClicked(val epochDay: Int) : Event()
        class OnMonthClicked(val agendaMonthYear: AgendaMonthYear) : Event()
        class OnYearClicked(val agendaYear: AgendaYear) : Event()
        object OnRequestCalendarPermissionClicked : Event()
        class ToggleEventVisibility(val agendaEvent: AgendaEvent): Event()
        object ToggleActive: Event()
    }

    enum class Section(val position: Int) {
        Day(0), Month(1), Year(2);

        companion object {
            fun fromInt(value: Int) = entries.first { it.position == value }
        }
    }
}
