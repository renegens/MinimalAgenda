package presentation.screens.paywall

import com.revenuecat.purchases.kmp.Package
import com.revenuecat.purchases.kmp.PurchasesError
import presentation.base.UiEffect
import presentation.base.UiEvent
import presentation.base.UiState

class PaywallContract {

    data class State(
        val offerings: List<Package> = emptyList(),
        val isProcessing: Boolean = false,
        val error: PurchasesError? = null,
        val needsReload: Boolean = false,
        val showAllPlans: Boolean = false,
        val selectedPackage: Package? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object OnPurchaseCompleted: Effect()
    }

    sealed class Event : UiEvent {
        class OnPurchaseItemClicked(val item: Package): Event()
        object DismissErrorDialog : Event()
        object GetOfferings : Event()
        object RestorePurchases : Event()
        object AllPlans : Event()
        object OpenPrivacy: Event()
        object OpenTermsOfUse: Event()
    }
}
