package presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import presentation.screens.calendar.CalendarScreen
import presentation.theme.AppTheme

@Composable
fun App() {
    KoinContext {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()

        AppTheme(
            appThemeMode = state.appTheme.appThemeMode, appColor = state.appTheme.appColor
        ) {
            CalendarScreen(appThemeMode = state.appTheme.appThemeMode,
                reloadEvents = state.shouldReloadEvents,
                onAddEvent = {
                    viewModel.setEvent(AppContract.Event.NewEvent)
                },
                onEditEvent = { viewModel.setEvent(AppContract.Event.EditExternal(it)) },
                notEligible = {
                    viewModel.setEvent(AppContract.Event.NotEligible)
                })
        }
    }
}