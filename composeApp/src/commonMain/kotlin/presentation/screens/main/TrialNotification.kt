package presentation.screens.main

import androidx.compose.runtime.Composable
import presentation.components.AppAlertDialog

@Composable
fun TrialNotificationError(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Missing notification permission",
        message = "Notification permissions haven’t been enabled, so we won’t be able to remind you when your trial ends. Your subscription will automatically renew in 7 days. If you’d like to cancel, please visit your Subscription Settings.",
        onDismissRequest = onDismiss
    )
}

@Composable
fun TrialNotificationSuccess(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Thank you",
        message = "Congratulations on starting your free trial! I hope you enjoy the experience. If you choose to cancel, you can do so anytime within the next 7 days by visiting your Subscription Settings.",
        onDismissRequest = onDismiss
    )
}