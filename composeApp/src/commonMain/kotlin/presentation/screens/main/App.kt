package presentation.screens.main

import androidx.compose.runtime.*
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import org.koin.compose.KoinContext
import presentation.components.PaperBackground
import presentation.screens.PaywallScreen
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme

@Composable
fun App(isSystemInDarkMode: Boolean) {

    LaunchedEffect(Unit) {
        Purchases.logLevel = LogLevel.DEBUG
        val config = PurchasesConfiguration(apiKey = "appl_yNbwOFpPkxlEQiXtVZmDQpMsMBt")
        Purchases.configure(configuration = config)
    }

    AppTheme(isSystemInDarkMode) {
        KoinContext {
            PaperBackground {


                //PaywallScreen(onPurchaseComplete = {}, onError = {})
                CalendarScreen()
            }
        }
    }
}