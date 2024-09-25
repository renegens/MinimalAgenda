package domain.model

sealed class DayListViewType {
    class Header(val agendaDay: AgendaDay) : DayListViewType()
    class Event(val event: AgendaEvent) : DayListViewType()
    class Divider(val epochDay: Int) : DayListViewType()
}