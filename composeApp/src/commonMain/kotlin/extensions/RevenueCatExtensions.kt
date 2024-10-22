package extensions

import com.revenuecat.purchases.kmp.CustomerInfo
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration

private val entitlementIdentifier = "Unlocked"
val REVENUECAT_API_KEY = "appl_yNbwOFpPkxlEQiXtVZmDQpMsMBt"

fun CustomerInfo.isSubscribed() = this.entitlements[entitlementIdentifier]?.isActive == true

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