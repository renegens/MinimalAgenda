package domain.model

sealed class DayListViewType {
    class Header(val agendaDay: AgendaDay) : DayListViewType()
    class Event(val event: AgendaEvent) : DayListViewType()
    class Divider(val epochDay: Long) : DayListViewType()

    // Extension property to generate a unique ID for each instance of DayListViewType
    val uniqueId: String
        get() = when (this) {
            is Header -> "Header-${agendaDay.epochDay}"  // Use a property of AgendaDay to generate the ID
            is Event -> "Event-${event.id}-${event.epochDay}"  // Use event's ID and epochDay to ensure uniqueness
            is Divider -> "Divider-$epochDay"  // Directly use the epochDay property for unique ID
        }
}