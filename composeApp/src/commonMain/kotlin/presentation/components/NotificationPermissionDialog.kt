package presentation.components

import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionDialog(onDismissRequest: () -> Unit) {
    AppAlertDialog(
        title = "Notifications Not Enabled",
        message = "It seems that notification permissions are currently not granted. To stay updated with important alerts and updates, you can enable notifications in your device’s system settings.",
        onContinueRequest = onDismissRequest,
    )
}