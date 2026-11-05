package presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import platform.CalendarPermissionHandler
import platform.sendEmail
import presentation.components.AppAlertDialog
import presentation.components.noRippleClickable
import presentation.screens.settings.SettingsContract.Effect
import presentation.screens.settings.SettingsContract.Event
import presentation.screens.settings.components.*
import presentation.theme.iosBody
import presentation.theme.iosExtraLargeTitle

@Composable
fun SettingsScreen(
    onDismiss: () -> Unit = {},
    onOpenDeviceSettings: () -> Unit = {}
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.Dismiss -> onDismiss.invoke()
                Effect.OpenDeviceSettings -> onOpenDeviceSettings.invoke()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissionState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
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
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) return

        // --- SECTION 1: CALENDAR CONNECTION ---
        SettingsItem {
            ItemTitle("Calendar Connection")
            Spacer(modifier = Modifier.height(8.dp))
            when (state.calendarPermission) {
                CalendarPermissionHandler.State.FullAccess -> {
                    // Changed "Synced" to "Connected" to sound more local/offline
                    ItemBody("✓ Access enabled. Your local events are visible.")
                }
                CalendarPermissionHandler.State.NotDetermined -> {
                    ItemBody("Connect your calendar to start planning with real data.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Allow Access", // Stronger CTA
                        Modifier
                            .padding(vertical = 8.dp)
                            .noRippleClickable { viewModel.setEvent(Event.OnRequestCalendarPermission) },
                        style = iosBody,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                CalendarPermissionHandler.State.Denied -> {
                    ItemBody("Access denied. Enable permissions in Settings to import events.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Open Device Settings",
                        Modifier
                            .padding(vertical = 8.dp)
                            .noRippleClickable { viewModel.setEvent(Event.OnOpenDeviceSettings) },
                        style = iosBody,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // --- SECTION 2: PREFERENCES ---
        SettingsItem {
            SwitchSettingItem(
                title = "Discreet Notifications", // Better than "Private Notifications"
                isChecked = state.privateNotifications,
                onCheckedChange = { viewModel.setEvent(Event.OnTogglePrivateNotifications(it)) }
            )
            // Focus on the benefit (Privacy) rather than the mechanism (Generic title)
            ItemBody("Hide event names on your lock screen for total privacy.")
        }

        SettingsItem {
            SwitchSettingItem(
                title = "Immersive Mode", // Sounds cooler than "Hide Bottom Nav"
                isChecked = state.hideBottomNavigation,
                onCheckedChange = { viewModel.setEvent(Event.OnToggleHideBottomNavigation(it)) }
            )
            ItemBody("Hide the navigation bar to maximize your screen space.")
        }

        SettingsItem {
            NotificationTimeInput(
                focusRequester = focusRequester,
                time = state.notificationTime,
                onTimeEntered = { viewModel.setEvent(Event.OnNotificationTimeEntered(it)) }
            )
        }

        // --- SECTION 3: APPEARANCE ---
        SettingsItem {
            ItemTitle("Appearance") // Standard OS terminology
            Spacer(modifier = Modifier.height(4.dp))
            ModeSelection(
                selectedTheme = state.appThemeMode
            ) { viewModel.setEvent(Event.OnModeSelected(it)) }
        }

        SettingsItem {
            ItemTitle("Accent Color")
            Spacer(modifier = Modifier.height(4.dp))
            ItemBody("Personalize the app to match your style.")
            Spacer(modifier = Modifier.height(16.dp))
            ColorSelection(state.appColor) { viewModel.setEvent(Event.OnColorSelected(it)) }
        }

        // --- SECTION 4: PRIVACY PROMISE (Condensed) ---
        SettingsItem(showDivider = true) {
            ItemTitle("Privacy & Security")
            Spacer(modifier = Modifier.height(16.dp))

            ItemHeadline("How it works")
            ItemBody("Minimal Agenda reads, creates, edits, and deletes events through your phone's system calendar. The app keeps no event database of its own.")

            Spacer(modifier = Modifier.height(12.dp))
            ItemHeadline("Zero Data Collection")
            ItemBody("Minimal Agenda collects no data. There are no analytics, ads, or developer servers. Your preferences and reminders stay on your phone.")
        }

        // --- SECTION 5: FEEDBACK ---
        SettingsItem {
            var showEmailError by remember { mutableStateOf(false) }
            if (showEmailError) {
                AppAlertDialog(
                    title = "Mail Unavailable",
                    message = "Please configure a mail account on your device to send feedback.",
                    onContinueRequest = { showEmailError = false }
                )
            }
            ItemTitle("Support")
            Spacer(modifier = Modifier.height(8.dp))
            ItemBody("Have a suggestion or found a bug? We'd love to help.")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Contact Support",
                Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClickable { sendEmail { showEmailError = true } },
                style = iosBody,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- SUPPORT DEVELOPMENT (FOSS build only) ---
        if (state.showDonate) {
            SettingsItem {
                ItemTitle("Support development")
                Spacer(modifier = Modifier.height(8.dp))
                ItemBody("Minimal Agenda is free and open source. If you find it useful, you can support its development.")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Support development",
                    Modifier
                        .padding(vertical = 8.dp)
                        .noRippleClickable { viewModel.setEvent(Event.OnOpenDonate) },
                    style = iosBody,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- SECTION 6: PRIVACY & TERMS ---
        SettingsItem(showDivider = state.showFocusModeSetup) {
            ItemTitle("Legal")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Privacy Policy",
                Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClickable { viewModel.setEvent(Event.OnOpenPrivacyPolicy) },
                style = iosBody,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Terms of Service",
                Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClickable { viewModel.setEvent(Event.OnOpenTermsOfService) },
                style = iosBody,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // --- SECTION 7: IOS FOCUS MODES ---
        if (state.showFocusModeSetup) {
            SettingsItem(showDivider = false) {
                ItemTitle("iOS Focus Mode Setup")
                Spacer(modifier = Modifier.height(16.dp))
                ItemBody("Ensure you receive alerts even when Do Not Disturb is on:")
                Spacer(modifier = Modifier.height(12.dp))
                // Bullet points for clearer instructions
                ItemBody("1. Open iOS Settings > Focus")
                ItemBody("2. Select a mode (e.g., Work)")
                ItemBody("3. Add this app to 'Allowed Notifications'")
            }
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
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) // Added padding to divider for better look
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
