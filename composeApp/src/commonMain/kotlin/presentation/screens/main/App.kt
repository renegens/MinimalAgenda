package presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.PaperBackground
import presentation.screens.calendar.CalendarScreen
import presentation.screens.paywall.PaywallScreen
import presentation.theme.AppTheme

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(isSystemInDarkMode: Boolean) {

    val viewModel: AppViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    AppTheme(isSystemInDarkMode) {
        PaperBackground {
            if (state.isSubscribed) {
                CalendarScreen()
            } else {
                PaywallScreen(
                    onPurchaseComplete = {
                        viewModel.setEvent(AppContract.Event.OnPurchaseCompleted)
                    })
            }
        }
    }
}