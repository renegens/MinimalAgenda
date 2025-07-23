package presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import platform.sendEmail
import presentation.components.AppAlertDialog
import presentation.components.noRippleClickable
import presentation.screens.settings.SettingsContract.Effect
import presentation.screens.settings.SettingsContract.Event
import presentation.screens.settings.components.ColorSelection
import presentation.screens.settings.components.ItemBody
import presentation.screens.settings.components.ItemHeadline
import presentation.screens.settings.components.ItemTitle
import presentation.screens.settings.components.ModeSelection
import presentation.screens.settings.components.NotificationTimeInput
import presentation.screens.settings.components.SwitchSettingItem
import presentation.theme.iosBody
import presentation.theme.iosExtraLargeTitle

@Composable
fun SettingsScreen(notEligible: () -> Unit, onDismiss: () -> Unit = {}) {
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
                Effect.Dismiss -> {
                    onDismiss.invoke()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {

            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = iosExtraLargeTitle,
                    text = "Settings",
                    color = MaterialTheme.colorScheme.tertiary,
                )
                IconButton(
                    onClick = { viewModel.setEvent(Event.OnDismiss) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Close settings",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            return
        }

        SettingsItem() {
            SwitchSettingItem(title = "Private Notifications",
                isChecked = state.privateNotifications,
                onCheckedChange = { viewModel.setEvent(Event.OnTogglePrivateNotifications(it)) })
            ItemBody(
                "When enabled, notifications will use a generic title (Premium feature)",
            )
        }

        SettingsItem() {
            SwitchSettingItem(title = "Hide Bottom Navigation",
                isChecked = state.hideBottomNavigation,
                onCheckedChange = { viewModel.setEvent(Event.OnToggleHideBottomNavigation(it)) })
            ItemBody(
                "When enabled, the bottom navigation bar will be hidden to maximize your viewing area",
            )
        }

        SettingsItem() {
            NotificationTimeInput(
                focusRequester = focusRequester,
                time = state.notificationTime,
                onTimeEntered = {
                    viewModel.setEvent(Event.OnNotificationTimeEntered(it))
                })
        }

        SettingsItem() {
            ItemTitle("Select your preferred mode")
            Spacer(modifier = Modifier.height(4.dp))
            ModeSelection(
                selectedTheme = state.appThemeMode
            ) { viewModel.setEvent(Event.OnModeSelected(it)) }
        }

        SettingsItem() {
            ItemTitle("Choose Your Color")
            Spacer(modifier = Modifier.height(4.dp))
            ItemBody("Customize colors to match your style (Premium feature)")
            Spacer(modifier = Modifier.height(16.dp))
            ColorSelection(state.appColor) { viewModel.setEvent(Event.OnColorSelected(it)) }
        }

        SettingsItem(showDivider = true) {
            ItemTitle("What happens with your data")
            Spacer(modifier = Modifier.height(16.dp))
            ItemHeadline("Accessing Existing Calendars")
            Spacer(modifier = Modifier.height(8.dp))
            ItemBody("The app will only access the calendar data already stored on your device. This includes events from any calendars synced to your device, such as iCloud, Google, or Outlook calendars")
            Spacer(modifier = Modifier.height(12.dp))
            ItemHeadline("Data Stays on Your Device")
            Spacer(modifier = Modifier.height(8.dp))
            ItemBody("Your calendar data is not sent or uploaded anywhere. It remains securely stored on your device, and the app only reads the data to display your events. No external servers or third parties have access to this information")
            Spacer(modifier = Modifier.height(12.dp))
            ItemHeadline("Privacy Assurance")
            Spacer(modifier = Modifier.height(8.dp))
            ItemBody("The app can't share your calendar data. It only uses the existing events to provide functionality directly on your device")
        }

        SettingsItem() {
            var showEmailError by remember { mutableStateOf(false) }
            if (showEmailError) {
                AppAlertDialog(
                    title = "Mail Error",
                    message = "Mail services are not available",
                    onContinueRequest = { showEmailError = false })
            }
            ItemTitle("Help us improve by sharing your feedback – we'd love to hear from you")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Send Feedback",
                Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                    .noRippleClickable { sendEmail { showEmailError = true } },
                style = iosBody,
                color = MaterialTheme.colorScheme.primary
            )
        }

        SettingsItem(showDivider = false) {
            ItemTitle("Don't Miss Important Notifications")
            Spacer(modifier = Modifier.height(16.dp))
            ItemBody("Open your Settings on iOS")
            Spacer(modifier = Modifier.height(4.dp))
            ItemBody("Go to Focus")
            Spacer(modifier = Modifier.height(4.dp))
            ItemBody("Select the specific mode (e.g. Work, Do Not Disturb)")
            Spacer(modifier = Modifier.height(4.dp))
            ItemBody("Under Allowed Notifications, add this app to ensure you receive important alerts")
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun SettingsItem(
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {

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