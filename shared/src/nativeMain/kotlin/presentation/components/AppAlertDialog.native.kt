package presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.default
import com.slapps.cupertino.destructive
import com.slapps.cupertino.theme.CupertinoTheme

@OptIn(ExperimentalCupertinoApi::class)
@Composable
actual fun AppAlertDialog(
    title: String,
    message: String,
    confirmButtonLabel: String,
    negativeButtonLabel: String?,
    onContinueRequest: () -> Unit,
    onNegativeRequest: (() -> Unit)?,
) {
    CupertinoTheme {
        CupertinoAlertDialog(
            properties = DialogProperties(dismissOnClickOutside = false),
            onDismissRequest = dialogDismissAction(onContinueRequest, onNegativeRequest),
            title = {
                CupertinoText(title)
            },
            message = {
                CupertinoText(message)
            }
        ) {
            if (negativeButtonLabel != null) {
                destructive(
                    onClick = {
                        onNegativeRequest?.invoke() ?: onContinueRequest()
                    }
                ) {
                    CupertinoText(negativeButtonLabel)
                }
            }
            default(
                onClick = {
                    onContinueRequest.invoke()
                }
            ) {
                CupertinoText(confirmButtonLabel)
            }
        }
    }
}
