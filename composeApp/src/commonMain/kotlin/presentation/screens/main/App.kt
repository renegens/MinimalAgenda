package presentation.screens.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import kotlinx.coroutines.flow.collectLatest
import navigation.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import platform.openSettings
import presentation.components.ComposableLifecycle
import presentation.screens.calendar.CalendarScreen
import presentation.screens.onboarding.Onboarding
import presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    KoinContext {
        val viewModel: AppViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        var showPaywall by remember { mutableStateOf(true) }
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
                    AppContract.Effect.DismissPaywall -> showPaywall = false
                    AppContract.Effect.PresentPaywall -> showPaywall = true
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
                        notEligible = {
                            viewModel.setEvent(AppContract.Event.NotEligible)
                        },
                        onOpenDeviceSettings = { viewModel.setEvent(AppContract.Event.OnOpenDeviceSettings) })
                }

                composable(Screen.Onboarding.route) {
                    Onboarding(
                        dismiss = { viewModel.setEvent(AppContract.Event.DismissOnboarding) },
                        openSettings = { viewModel.setEvent(AppContract.Event.OnOpenDeviceSettings) }
                    )
                }
            }
            if (showPaywall) {
                val listener = object : PaywallListener {
                    override fun onPurchaseCompleted(
                        customerInfo: CustomerInfo,
                        storeTransaction: StoreTransaction
                    ) {
                        viewModel.setEvent(AppContract.Event.OnPurchased)
                    }

                    override fun onPurchaseCancelled() {
                        viewModel.setEvent(AppContract.Event.OnDismissPaywall)
                    }

                    override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    }
                }

                val options = PaywallOptions(
                    dismissRequest = {
                        viewModel.setEvent(AppContract.Event.OnDismissPaywall)
                    }
                ) {
                    this.listener = listener
                    shouldDisplayDismissButton = true
                }

                Paywall(options = options)
            }
        }
    }
}

@Preview
@Composable
fun MainApp() {
    App()
}