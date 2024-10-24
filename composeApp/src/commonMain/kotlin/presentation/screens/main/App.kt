package presentation.screens.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import presentation.screens.calendar.CalendarScreen
import presentation.screens.paywall.PaywallScreen
import presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    KoinContext {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        AppTheme(
            appThemeMode = state.appTheme.appThemeMode, appColor = state.appTheme.appColor
        ) {

            if (state.showTrialError) {
                TrialNotificationError { viewModel.setEvent(AppContract.Event.DismissTrialNotification) }
            }

            if (state.showTrialSuccess) {
                TrialNotificationSuccess { viewModel.setEvent(AppContract.Event.DismissTrialNotification) }
            }

            CalendarScreen(appThemeMode = state.appTheme.appThemeMode,
                forceEventLoad = state.forceEventLoad,
                onAddEvent = {
                    viewModel.setEvent(AppContract.Event.NewEvent)
                },
                onEditEvent = { viewModel.setEvent(AppContract.Event.EditExternal(it)) },
                notEligible = {
                    viewModel.setEvent(AppContract.Event.NotEligible)
                })

            if (state.showPaywall) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = {
                        viewModel.setEvent(AppContract.Event.DismissPaywall)
                    },
                    sheetState = sheetState
                ) {
                    PaywallScreen(
                        onPurchaseComplete = { viewModel.setEvent(AppContract.Event.OnPurchased(it)) },
                        onCancel = { viewModel.setEvent(AppContract.Event.DismissPaywall) })
                }
            }
        }
    }
}