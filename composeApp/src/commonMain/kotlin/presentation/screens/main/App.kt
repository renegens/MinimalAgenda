package presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.screens.calendar.CalendarScreen
import presentation.screens.paywall.PaywallScreen
import presentation.theme.AppTheme

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    KoinContext {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()

        state.appTheme?.let { theme ->
            AppTheme(
                appTheme = theme,
            ) {
                if (state.isSubscribed) {
                    CalendarScreen(appTheme = theme)
                } else {
                    PaywallScreen(onPurchaseComplete = {
                        viewModel.setEvent(AppContract.Event.OnPurchaseCompleted)
                    })
                }
            }
        }
    }
}