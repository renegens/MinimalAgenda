package presentation.screens.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import navigation.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import platform.openSettings
import presentation.screens.calendar.CalendarScreen
import presentation.screens.onboarding.Onboarding
import presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val navController = rememberNavController()
        val start = if (state.shouldShowOnboarding) {
            Screen.Onboarding
        } else {
            Screen.Agenda
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest {
                when (it) {
                    AppContract.Effect.CloseOnboardingAndGoToCalendar -> navController.navigate(
                        Screen.Agenda.route
                    )

                    AppContract.Effect.OpenDeviceAppSettings -> openSettings()
                }
            }
        }

        AppTheme(
            appThemeMode = state.appTheme.appThemeMode, appColor = state.appTheme.appColor
        ) {
            NavHost(
                navController = navController, startDestination = start.route
            ) {
                composable(Screen.Agenda.route) {
                    CalendarScreen(appThemeMode = state.appTheme.appThemeMode,
                        forceEventLoad = state.forceEventLoad,
                        onAddEvent = {
                            viewModel.setEvent(AppContract.Event.NewEvent)
                        },
                        onEditEvent = { viewModel.setEvent(AppContract.Event.EditExternal(it)) },
                        onOpenDeviceSettings = { viewModel.setEvent(AppContract.Event.OnOpenDeviceSettings) })
                }

                composable(Screen.Onboarding.route) {
                    Onboarding(
                        dismiss = { viewModel.setEvent(AppContract.Event.DismissOnboarding) },
                        openSettings = { viewModel.setEvent(AppContract.Event.OnOpenDeviceSettings) }
                    )
                }
            }
        }

}

@Preview
@Composable
fun MainApp() {
    App()
}