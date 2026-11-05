package presentation.components

import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionDialog(onDismissRequest: () -> Unit) {
    AppAlertDialog(
        title = "Notifications Disabled",
        message = "Notifications are currently disabled. Enable them in your device settings to receive event updates.",
        onContinueRequest = onDismissRequest,
    )
}