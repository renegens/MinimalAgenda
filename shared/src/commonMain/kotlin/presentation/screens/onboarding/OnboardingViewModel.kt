package presentation.screens.onboarding

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import platform.CalendarPermissionHandler
import presentation.base.BaseViewModel
import presentation.screens.onboarding.OnboardingContract.*

class OnboardingViewModel(
    private val calendarPermissionHandler: CalendarPermissionHandler
) : BaseViewModel<Event, State, Effect>() {

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.OnDismissOnboarding -> setEffect { Effect.CloseOnboarding }
            Event.OnRequestCalendarPermission -> viewModelScope.launch {
                when (val state = calendarPermissionHandler.requestCalendarPermission()) {
                    CalendarPermissionHandler.State.FullAccess -> setEffect { Effect.CloseOnboarding }
                    else -> setState { copy(calendarPermissionState = state) }
                }
            }
        }
    }
}