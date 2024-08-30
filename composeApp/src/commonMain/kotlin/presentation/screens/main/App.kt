package presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.revenuecat.purchases.kmp.Purchases
import extensions.initializeRevenueCat
import extensions.isSubscribed
import org.koin.compose.KoinContext
import presentation.components.PaperBackground
import presentation.screens.PaywallScreen
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme

@Composable
fun App(isSystemInDarkMode: Boolean) {

    var isSubscribed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        initializeRevenueCat()
        Purchases.sharedInstance.getCustomerInfo(
            onError = { error ->
                println(error)
                isSubscribed = false
            },
            onSuccess = { customerInfo ->
                isSubscribed = customerInfo.isSubscribed()
            })
    }

    AppTheme(isSystemInDarkMode) {
        KoinContext {
            PaperBackground {
                if (isSubscribed) {
                    CalendarScreen()
                } else {
                    PaywallScreen(
                        onPurchaseComplete = { isSubscribed = true },
                        onError = { println(it) })
                }
            }
        }
    }
}