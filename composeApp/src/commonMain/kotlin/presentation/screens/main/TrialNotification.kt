package presentation.screens.main

import androidx.compose.runtime.Composable
import io.github.alexzhirkevich.cupertino.CupertinoActionSheet
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.cancel
import io.github.alexzhirkevich.cupertino.default
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import presentation.components.AppAlertDialog
import presentation.theme.iosBody

@Composable
fun TrialNotificationError(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Notifications Disabled",
        message = "We won’t be able to remind you when your trial ends. Your subscription will renew in 7 days. To cancel, go to Subscription Settings.",
        onContinueRequest = onDismiss
    )
}

@Composable
fun TrialNotificationSuccess(onDismiss: () -> Unit) {
    AppAlertDialog(
        title = "Welcome to your free trial!",
        message = "Enjoy the experience. Cancel anytime in the next 7 days via Subscription Settings.",
        onContinueRequest = onDismiss
    )
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun TrialNotificationAskForNotificationPermission(onContinue: () -> Unit, onCancel: () -> Unit) {
    CupertinoTheme {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = onCancel,
            title = {
                CupertinoText("Trial Reminder")
            },
            message = {
                CupertinoText(
                    "Enable notifications for a reminder 2 days before your trial ends.",
                    style = iosBody
                )
            },
        ) {
            default(
                onClick = onContinue
            ) {
                CupertinoText("Continue")
            }
            cancel(
                onClick = onCancel
            ) {
                CupertinoText("Cancel")
            }
        }
    }
}