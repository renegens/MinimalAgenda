package presentation.screens.paywall

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PackageType
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import data.PRIVACY_URL
import data.TERMS_OF_USE_URL
import extensions.isSubscribed
import platform.Browser
import presentation.base.BaseViewModel
import presentation.screens.paywall.PaywallContract.Effect
import presentation.screens.paywall.PaywallContract.Event
import presentation.screens.paywall.PaywallContract.State

class PaywallViewModel(private val browser: Browser) :
    BaseViewModel<Event, State, Effect>() {

    init {
        getOfferings()
    }

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.OnOfferingSelected -> setState { copy(selectedPackage = event.offer) }
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
            Event.AllPlans -> setState { copy(showAllPlans = !showAllPlans) }
            Event.OnProceedButtonClicked -> {
                if (uiState.value.needsReload || uiState.value.offerings.isEmpty()) {
                    getOfferings()
                    return
                }

                val item = uiState.value.selectedPackage
                item?.let { handlePurchase(it) } ?: setState {
                    copy(
                        error = PurchasesError(
                            underlyingErrorMessage = "No Product Selected",
                            code = PurchasesErrorCode.ConfigurationError
                        )
                    )
                }
            }
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
                    var selectedItem: Package? = null
                    val sortedItems = mutableListOf<Package>()
                    items.firstOrNull { it.packageType == PackageType.MONTHLY }?.let {
                        sortedItems.add(it)
                    }
                    items.firstOrNull { it.packageType == PackageType.ANNUAL }?.let {
                        sortedItems.add(it)
                        selectedItem = it
                    }
                    items.firstOrNull { it.packageType == PackageType.LIFETIME }?.let {
                        sortedItems.add(it)
                    }

                    setState {
                        copy(
                            offerings = sortedItems,
                            isProcessing = false,
                            needsReload = false,
                            selectedPackage = selectedItem ?: sortedItems.first()
                        )
                    }
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