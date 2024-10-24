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
        val showPaywall: Boolean = false,
        val showTrialError: Boolean = false,
        val showTrialSuccess: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect

    sealed class Event : UiEvent {
        class EditExternal(val agendaEvent: AgendaEvent): Event()
        object NewEvent: Event()
        object DismissTrialNotification: Event()
        object NotEligible: Event()
        object DismissPaywall: Event()
        class OnPurchased(val purchase: Package?): Event()
    }
}
