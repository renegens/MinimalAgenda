package presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.KoinContext
import presentation.components.PaperBackground
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme
import com.revenuecat.purchases.kmp.CustomerInfo
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Offering
import com.revenuecat.purchases.kmp.Offerings
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.all
import com.revenuecat.purchases.kmp.identifier
import com.revenuecat.purchases.kmp.models.Period
import com.revenuecat.purchases.kmp.models.Price
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.serverDescription

@Composable
fun App(isSystemInDarkMode: Boolean) {

    Purchases.logLevel = LogLevel.DEBUG

    val config = PurchasesConfiguration(apiKey = "appl_yNbwOFpPkxlEQiXtVZmDQpMsMBt")
    Purchases.configure(configuration = config)
    val list = mutableStateListOf<Offering>()
    Purchases.sharedInstance.getOfferings(onError = {
        println("Error")
    }, onSuccess = {
            offerings: Offerings ->
        list.addAll(offerings.all.values)
    })

    AppTheme(isSystemInDarkMode) {
        KoinContext {
            PaperBackground {

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    list.forEach {
                        Text(text = it.serverDescription, color = Color.Red)
                    }
                }

                //CalendarScreen()
            }
        }
    }
}