package presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import presentation.theme.DMSans18Regular

@Composable
fun AppAlertDialog(title: String, message: String, onDismissRequest: () -> Unit) {

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
}