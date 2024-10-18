package presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import platform.sendEmail
import presentation.components.AppAlertDialog
import presentation.components.PaperBackground
import presentation.screens.settings.SettingsContract.*
import presentation.screens.settings.components.ColorSelection
import presentation.screens.settings.components.ExplanationText
import presentation.screens.settings.components.ItemText
import presentation.screens.settings.components.ModeSelection
import presentation.screens.settings.components.NotificationTimeInput
import presentation.screens.settings.components.SwitchSettingItem
import presentation.theme.iosBody
import presentation.theme.iosExtraLargeTitle

@Composable
fun SettingsScreen(notEligible: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.NotEligible -> {
                    focusManager.clearFocus()
                    notEligible.invoke()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .verticalScroll(state = rememberScrollState())
    ) {
        PaperBackground(state.appThemeMode) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    style = iosExtraLargeTitle,
                    text = "Settings",
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (state.isLoading) {
            return
        }

        SettingsItem(state.appThemeMode) {
            SwitchSettingItem(title = "Private Notifications",
                isChecked = state.privateNotifications,
                onCheckedChange = { viewModel.setEvent(Event.OnTogglePrivateNotifications(it)) })
            ExplanationText(
                "When enabled, notifications will use a generic title. (Premium)",
            )
        }

        SettingsItem(state.appThemeMode) {
            NotificationTimeInput(
                focusRequester = focusRequester,
                time = state.notificationTime,
                onTimeEntered = {
                    viewModel.setEvent(Event.OnNotificationTimeEntered(it))
                })
        }

        SettingsItem(state.appThemeMode) {
            ItemText("Select your preferred mode")
            Spacer(modifier = Modifier.height(4.dp))
            ModeSelection(
                selectedTheme = state.appThemeMode
            ) { viewModel.setEvent(Event.OnModeSelected(it)) }
        }

        SettingsItem(state.appThemeMode) {
            ItemText("Select your preferred color (Premium)")
            Spacer(modifier = Modifier.height(16.dp))
            ColorSelection(state.appColor) { viewModel.setEvent(Event.OnColorSelected(it)) }
        }



        SettingsItem(state.appThemeMode) {
            var showEmailError by remember { mutableStateOf(false) }
            if (showEmailError) {
                AppAlertDialog(
                    title = "Mail Error",
                    message = "Mail services are not available",
                    onDismissRequest = { showEmailError = false })
            }
            ItemText("Help us improve by sharing your feedback – we’d love to hear from you!")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Send Feedback",
                Modifier.clickable { sendEmail { showEmailError = true } },
                style = iosBody,
                color = MaterialTheme.colorScheme.primary
            )
        }

        SettingsItem(state.appThemeMode, showDivider = false) {
            ItemText("Don’t Miss Important Notifications")
            Spacer(modifier = Modifier.height(16.dp))
            ExplanationText("Open your Settings on iOS.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Go to Focus.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Select the specific mode (e.g. Work, Do Not Disturb).")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Under Allowed Notifications, add this app to ensure you receive important alerts.")
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun SettingsItem(
    appThemeMode: AppThemeMode,
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    PaperBackground(appThemeMode) {
        Column {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                content.invoke()
            }
            Spacer(modifier = Modifier.height(32.dp))
            if (showDivider) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}