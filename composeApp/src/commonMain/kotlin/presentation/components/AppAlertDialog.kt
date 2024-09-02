package presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import presentation.theme.CustomColor
import presentation.theme.DMSans18Regular

@Composable
fun AppAlertDialog(title: String, message: String, onDismissRequest: () -> Unit) {

    val colors = if (isSystemInDarkTheme()) {
        CustomColor.White_500 to CustomColor.Black_900
    } else {
        CustomColor.Black_900 to CustomColor.White_500
    }

    AlertDialog(
        title = {
            Text(text = title, color = MaterialTheme.colorScheme.primary)
        },
        containerColor = colors.first,
        text = {
            Text(
                text = message,
                style = DMSans18Regular,
                fontSize = 16.sp,
                color = colors.second
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
}