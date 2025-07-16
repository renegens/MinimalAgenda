package presentation.screens.calendar

import domain.model.AgendaDay
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import domain.model.DayListViewType
import extensions.today
import kotlinx.datetime.LocalDate
import platform.CalendarPermissionHandler
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class CalendarContract {

    data class State(
        val days: List<AgendaDay> = emptyList(),
        val agendaEvents: List<AgendaEvent> = emptyList(),
        val flatDaysAndEvents: List<DayListViewType> = emptyList(),
        val showAll: Boolean = false,
        val scrollToNow: Boolean = true,
        val currentDate: LocalDate = today,
        val calendarPermission: CalendarPermissionHandler.State = CalendarPermissionHandler.State.NotDetermined,
        val showSwipeToolTip: Boolean = false,
        val isDemoMode: Boolean = false,
    ) : UiState

    sealed class Effect : UiEffect {
        object ScrollDayToToday : Effect()
        object ScrollMonthToToday : Effect()
        object ScrollYearToToday : Effect()
        class GoToDay(val epochDay: Int) : Effect()
        class GoToMonth(val agendaMonthYear: AgendaMonthYear) : Effect()
        class GoToYear(val agendaYear: AgendaYear) : Effect()
        object ShowPaywall : Effect()
        class ShowEdit(val agendaEvent: AgendaEvent) : Effect()
        object NotificationPermissionMissing : Effect()
        object ShowSheetTip : Effect()
        object DismissSheet : Effect()
    }

    sealed class Event : UiEvent {
        object InitialDataLoad : Event()
        object ForceReload : Event()
        class OnNowClicked(val section: Section) : Event()
        class OnDayClicked(val epochDay: Int) : Event()
        class OnMonthClicked(val agendaMonthYear: AgendaMonthYear) : Event()
        class OnYearClicked(val agendaYear: AgendaYear) : Event()
        object OnRequestCalendarPermissionClicked : Event()
        class ToggleEventVisibility(val agendaEvent: AgendaEvent) : Event()
        class OnEditEvent(val agendaEvent: AgendaEvent) : Event()
        object ToggleActive : Event()
        object HideSwipeToolTip : Event()
        object DismissBottomSheet : Event()
        object ShowPaywallFromBottomSheet: Event()
        object ShowBottomSheet: Event()
        class Notify(val agendaEvent: AgendaEvent) : Event()
    }

    enum class Section(val position: Int) {
        Day(0), Month(1), Year(2), Settings(3);

        companion object {
            fun fromInt(value: Int) = entries.first { it.position == value }
        }
    }
}
