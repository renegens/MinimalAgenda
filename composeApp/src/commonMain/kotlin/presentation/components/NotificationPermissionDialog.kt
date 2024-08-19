package presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun NotificationPermissionDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = "Notifications Not Enabled", color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Text(text = "It seems that notification permissions are currently not granted. To stay updated with important alerts and updates, you can enable notifications in your device’s system settings.")
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Ok")
            }
        }
    )
}