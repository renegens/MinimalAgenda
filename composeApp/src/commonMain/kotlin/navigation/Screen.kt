package navigation

const val ID = "id"

sealed class Screen(
    var route: String,
) {
    object Paywall : Screen("paywall")
    object Agenda: Screen("agenda")
    object CreateEvent : Screen("event")
    object UpdateEvent : Screen(
        "updateEvent/{$ID}") {
        fun route(eventId: String) = "updateEvent/$eventId"
    }
}