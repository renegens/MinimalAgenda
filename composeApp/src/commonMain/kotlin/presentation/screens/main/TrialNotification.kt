package presentation.screens.main

import androidx.compose.runtime.Composable
import presentation.components.AppAlertDialog

@Composable
fun TrialNotificationError(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Missing notification permission",
        message = "Notification permissions haven’t been enabled, so we won’t be able to remind you when your trial ends. Your subscription will automatically renew in 7 days. If you’d like to cancel, please visit your Subscription Settings.",
        onContinueRequest = onDismiss
    )
}

@Composable
fun TrialNotificationSuccess(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Thank you",
        message = "Congratulations on starting your free trial! I hope you enjoy the experience. If you choose to cancel, you can do so anytime within the next 7 days by visiting your Subscription Settings.",
        onContinueRequest = onDismiss
    )
}

@Composable
fun TrialNotificationAskForNotificationPermission(onContinue: () -> Unit, onCancel: () -> Unit) {
    AppAlertDialog(
        title = "Stay Informed About Your Trial!",
        message = "To help you make the most of your trial, we’d like to send you a reminder 2 days before it ends.\n" +
                "\n" +
                "Would you like to allow notifications for this reminder?",
        confirmButtonLabel = "Continue",
        onContinueRequest = onContinue,
        onCancelRequest = onCancel
    )
}