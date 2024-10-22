package presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import getPlatform
import io.github.alexzhirkevich.cupertino.CupertinoAlertDialog
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.default
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import presentation.theme.DMSans18Regular

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AppAlertDialog(title: String, message: String, onDismissRequest: () -> Unit) {
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
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Ok")
                }
            }
        )
    } else {
        CupertinoTheme {
            CupertinoAlertDialog(
                onDismissRequest = onDismissRequest,
                title = {
                    CupertinoText(title)
                },
                message = {
                    CupertinoText(message)
                }
            ) {
                default(
                    onClick = {
                        onDismissRequest.invoke()
                    }
                ) {
                    CupertinoText("OK")
                }
            }
        }
    }
}