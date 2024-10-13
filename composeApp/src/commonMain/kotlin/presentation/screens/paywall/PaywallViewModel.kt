package presentation.screens.paywall

import com.revenuecat.purchases.kmp.CustomerInfo
import com.revenuecat.purchases.kmp.Package
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesError
import com.revenuecat.purchases.kmp.PurchasesErrorCode
import data.PRIVACY_URL
import data.TERMS_OF_USE_URL
import extensions.isSubscribed
import platform.Browser
import presentation.base.BaseViewModel
import presentation.screens.paywall.PaywallContract.*

class PaywallViewModel(private val browser: Browser) :
    BaseViewModel<Event, State, Effect>() {

    init {
        getOfferings()
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnPurchaseItemClicked -> handlePurchase(event.item)
            Event.DismissErrorDialog -> setState {
                copy(
                    isProcessing = false,
                    error = null,
                    needsReload = true,
                )
            }

            Event.GetOfferings -> getOfferings()
            Event.RestorePurchases -> restorePurchases()
            Event.OpenPrivacy -> browser.openUrl(PRIVACY_URL)
            Event.OpenTermsOfUse -> browser.openUrl(TERMS_OF_USE_URL)
            Event.AllPlans -> TODO()
        }
    }

    private fun handlePurchase(packageItem: Package) {
        setState { copy(isProcessing = true) }
        Purchases.sharedInstance.purchase(
            packageToPurchase = packageItem,
            onError = { error, userCancelled ->
                if (userCancelled) {
                    setState { copy(isProcessing = false, error = null) }
                } else {
                    setState { copy(isProcessing = false, error = error) }
                }
            },
            onSuccess = { _, customerInfo -> onPurchaseComplete(customerInfo) }
        )
    }

    private fun getOfferings() {
        setState { copy(isProcessing = true) }
        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                setState { copy(error = error, needsReload = true, isProcessing = false) }
            },
            onSuccess = { offerings ->
                val items = offerings.current?.availablePackages?.takeUnless { it.isEmpty() }
                if (items.isNullOrEmpty()) {
                    setState { copy(needsReload = true, isProcessing = false) }
                } else {
                    setState { copy(offerings = items, isProcessing = false) }
                }
            })
    }

    private fun restorePurchases() {
        setState { copy(isProcessing = true) }
        Purchases.sharedInstance.restorePurchases(
            onError = { error ->
                setState { copy(error = error) }
            },
            onSuccess = { customerInfo -> onPurchaseComplete(customerInfo) }
        )
    }

    private fun onPurchaseComplete(customerInfo: CustomerInfo) {
        setState { copy(isProcessing = false) }
        if (customerInfo.isSubscribed()) {
            setEffect { Effect.OnPurchaseCompleted }
        } else {
            setState {
                copy(
                    error = PurchasesError(
                        code = PurchasesErrorCode.CustomerInfoError,
                        underlyingErrorMessage = "We can't verify that purchases can be restored"
                    )
                )
            }
        }
    }
}