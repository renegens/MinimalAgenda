package navigation

const val ID = "id"

sealed class Screen(
    var route: String,
) {
    object Agenda: Screen("agenda")
    object CreateEvent : Screen("event")
    object UpdateEvent : Screen(
        "updateEvent/{$ID}") {
        fun route(eventId: String) = "updateEvent/$eventId"
    }
    object Onboarding: Screen(route = "onboarding")
}