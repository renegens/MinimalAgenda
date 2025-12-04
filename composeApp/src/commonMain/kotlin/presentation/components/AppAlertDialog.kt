package presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import getPlatform
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.default
import com.slapps.cupertino.destructive
import com.slapps.cupertino.theme.CupertinoTheme
import presentation.theme.DMSans18Regular

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AppAlertDialog(
    title: String,
    message: String,
    confirmButtonLabel: String = "OK",
    negativeButtonLabel: String? = null,
    onContinueRequest: () -> Unit,
    onNegativeRequest: (() -> Unit)? = null
) {
    if (getPlatform().isAndroid) {
        AlertDialog(
            title = {
                Text(text = title, color = MaterialTheme.colorScheme.inverseSurface)
            },
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            text = {
                Text(
                    text = message,
                    style = DMSans18Regular,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            },
            onDismissRequest = onContinueRequest,
            confirmButton = {
                TextButton(
                    onClick = onContinueRequest
                ) {
                    Text(confirmButtonLabel)
                }
            },
            dismissButton = if (negativeButtonLabel != null) {
                {
                    TextButton(
                        onClick = { onNegativeRequest?.invoke() ?: onContinueRequest() }
                    ) {
                        Text(negativeButtonLabel)
                    }
                }
            } else null
        )
    } else {
        CupertinoTheme {
            CupertinoAlertDialog(
                properties = DialogProperties(dismissOnClickOutside = false),
                onDismissRequest = onContinueRequest,
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
}