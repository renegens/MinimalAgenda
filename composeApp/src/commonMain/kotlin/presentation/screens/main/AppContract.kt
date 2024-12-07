package presentation.screens.main

import com.revenuecat.purchases.kmp.models.Package
import domain.model.AgendaEvent
import domain.model.AppTheme
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class AppContract {

    data class State(
        val forceEventLoad: Int = 0,
        val appTheme: AppTheme = AppTheme.Default,
        val showTrialNotificationError: Boolean = false,
        val showTrialNotificationSuccess: Boolean = false,
        val showNotificationPermissionInfo: Boolean = false
    ) : UiState {

        fun isPresentingNotificationOnboarding() = showTrialNotificationSuccess || showTrialNotificationError || showNotificationPermissionInfo
    }

    sealed class Effect : UiEffect {
        object PresentPaywall: Effect()
        object DismissPaywall: Effect()
    }

    sealed class Event : UiEvent {
        class EditExternal(val agendaEvent: AgendaEvent): Event()
        object NewEvent: Event()
        object DismissTrialNotification: Event()
        class  RequestNotificationPermission(val shouldRequest: Boolean): Event()
        object NotEligible: Event()
        object OnDismissPaywall: Event()
        class OnPurchased(val purchase: Package?): Event()
        object CheckPaywall: Event()
    }
}
