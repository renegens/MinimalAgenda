package presentation.screens.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import navigation.Screen
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.ComposableLifecycle
import presentation.screens.calendar.CalendarScreen
import presentation.screens.onboarding.Onboarding
import presentation.screens.paywall.PaywallScreen
import presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    KoinContext {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val paywallSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        var showPaywallBottomSheet by remember { mutableStateOf(false) }
        val navController = rememberNavController()
        val start = if (state.shouldShowOnboarding) {
            Screen.Onboarding
        } else {
            Screen.Agenda
        }

        ComposableLifecycle {
            if (it == Lifecycle.Event.ON_START) {
                viewModel.setEvent(AppContract.Event.CheckPaywall)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest {
                when (it) {
                    AppContract.Effect.DismissPaywall -> scope.launch { paywallSheetState.hide() }
                        .invokeOnCompletion {
                            if (!paywallSheetState.isVisible) {
                                showPaywallBottomSheet = false
                            }
                        }

                    AppContract.Effect.PresentPaywall -> showPaywallBottomSheet = true
                    AppContract.Effect.CloseOnboardingAndGoToCalendar -> navController.navigate(
                        Screen.Agenda.route
                    )
                }
            }
        }

        AppTheme(
            appThemeMode = state.appTheme.appThemeMode, appColor = state.appTheme.appColor
        ) {

            if (state.showTrialNotificationError) {
                TrialNotificationError { viewModel.setEvent(AppContract.Event.DismissTrialNotification) }
            }

            if (state.showTrialNotificationSuccess) {
                TrialNotificationSuccess { viewModel.setEvent(AppContract.Event.DismissTrialNotification) }
            }

            if (state.showNotificationPermissionInfo) {
                TrialNotificationAskForNotificationPermission(onContinue = {
                    viewModel.setEvent(
                        AppContract.Event.RequestNotificationPermission(true)
                    )
                }, onCancel = {
                    viewModel.setEvent(
                        AppContract.Event.RequestNotificationPermission(false)
                    )
                })
            }

            NavHost(
                navController = navController,
                startDestination = start.route
            ) {
                composable(Screen.Agenda.route) {
                    CalendarScreen(
                        appThemeMode = state.appTheme.appThemeMode,
                        forceEventLoad = state.forceEventLoad,
                        onAddEvent = {
                            viewModel.setEvent(AppContract.Event.NewEvent)
                        },
                        onEditEvent = { viewModel.setEvent(AppContract.Event.EditExternal(it)) },
                        notEligible = {
                            viewModel.setEvent(AppContract.Event.NotEligible)
                        },
                        isPresentingNotificationOnboarding = state.isPresentingNotificationOnboarding()
                    )
                }

                composable(Screen.Onboarding.route) {
                    Onboarding(
                        dismiss = { viewModel.setEvent(AppContract.Event.DismissOnboarding) }
                    )
                }
            }
            if (showPaywallBottomSheet) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = {
                        viewModel.setEvent(AppContract.Event.OnDismissPaywall)
                    },
                    sheetState = paywallSheetState
                ) {
                    PaywallScreen(
                        onPurchaseComplete = { viewModel.setEvent(AppContract.Event.OnPurchased(it)) },
                        onCancel = { viewModel.setEvent(AppContract.Event.OnDismissPaywall) })
                }
            }
        }
    }
}