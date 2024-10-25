package extensions

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PackageType
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private val entitlementIdentifier = "Unlocked"
val REVENUECAT_API_KEY = "appl_yNbwOFpPkxlEQiXtVZmDQpMsMBt"

fun CustomerInfo.isSubscribed() =
    this.entitlements[entitlementIdentifier]?.isActive == true

fun initializeRevenueCat(debug: Boolean = false) {
    if (debug) {
        Purchases.logLevel = LogLevel.DEBUG
    }
    val config = PurchasesConfiguration(apiKey = REVENUECAT_API_KEY)
    Purchases.configure(configuration = config)
}

fun guardPremium(onSubscribed: () -> Unit, onPaywall: () -> Unit) {
    Purchases.sharedInstance.getCustomerInfo(onError = { onPaywall.invoke() },
        onSuccess = { customerInfo ->
            if (customerInfo.isSubscribed()) {
                onSubscribed.invoke()
            } else {
                onPaywall.invoke()
            }
        })
}

suspend fun isPremium(): Boolean = suspendCancellableCoroutine { continuation ->
    Purchases.sharedInstance.getCustomerInfo(
        onError = {
            continuation.resume(false) // Invoke paywall if there's an error
        },
        onSuccess = { customerInfo: CustomerInfo ->
            if (customerInfo.isSubscribed()) {
                continuation.resume(true) // Invoke onSubscribed if the user is subscribed
            } else {
                continuation.resume(false) // Invoke paywall if the user is not subscribed
            }
        }
    )
}

fun Package.getOfferText(): String {
    return when (packageType) {
        PackageType.MONTHLY -> "Full access for just ${storeProduct.price.formatted}/mo"
        PackageType.ANNUAL -> "First week free, then ${storeProduct.price.formatted}/yr"
        PackageType.LIFETIME -> "Full access for just ${storeProduct.price.formatted}"
        else -> ""
    }
}

fun Package.getOfferContinueButtonText(): String {
    return when (packageType) {
        PackageType.ANNUAL -> "Start Your FREE Trial"
        else -> "Continue"
    }
}