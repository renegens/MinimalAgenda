package presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.PaperBackgroundColumn
import presentation.components.SettingsHeader
import presentation.screens.settings.SettingsContract.*
import presentation.screens.settings.components.ColorSelection
import presentation.screens.settings.components.ExplanationText
import presentation.screens.settings.components.ItemText
import presentation.screens.settings.components.ModeSelection
import presentation.screens.settings.components.NotificationTimeInput
import presentation.screens.settings.components.SwitchSettingItem

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .verticalScroll(state = rememberScrollState())
    ) {
        PaperBackgroundColumn(state.appThemeMode) {
            SettingsHeader("Settings")
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.isLoading) {
            return
        }

        PaperBackgroundColumn(state.appThemeMode) {
            SwitchSettingItem(title = "Private Notifications",
                isChecked = state.privateNotifications,
                onCheckedChange = { viewModel.setEvent(Event.OnTogglePrivateNotifications(it)) })
            ExplanationText(
                "When enabled, notifications will use a generic title.",
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        PaperBackgroundColumn(state.appThemeMode) {
            NotificationTimeInput(time = state.notificationTime, onTimeEntered = {
                viewModel.setEvent(Event.OnNotificationTimeEntered(it))
            })
            Spacer(modifier = Modifier.height(40.dp))
        }

        PaperBackgroundColumn(state.appThemeMode) {
            ItemText("Select your preferred mode")
            Spacer(modifier = Modifier.height(4.dp))
            ModeSelection(
                selectedTheme = state.appThemeMode
            ) { viewModel.setEvent(Event.OnModeSelected(it)) }
            Spacer(modifier = Modifier.height(32.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ItemText("Select your preferred color")
            Spacer(modifier = Modifier.height(16.dp))
            ColorSelection(state.appColor) { viewModel.setEvent(Event.OnColorSelected(it)) }
            Spacer(modifier = Modifier.height(48.dp))
        }

        PaperBackgroundColumn(state.appThemeMode) {
            ItemText("Don’t Miss Important Notifications")
            Spacer(modifier = Modifier.height(16.dp))
            ExplanationText("Open your Settings on iOS.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Go to Focus.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Select the specific mode (e.g. Work, Do Not Disturb).")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Under Allowed Notifications, add this app to ensure you receive important alerts.")
            Spacer(modifier = Modifier.height(64.dp))
        }

//        item {
//            SettingsItem(title = "Send feedback",
//                onClick = { viewModel.setEvent(Event.OnSentFeedback) })
//        }
    }
}