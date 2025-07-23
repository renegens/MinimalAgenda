package presentation.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import presentation.theme.DMSans18Medium
import presentation.theme.TextFieldStyle

@Composable
internal fun NotificationTimeInput(focusRequester: FocusRequester, time: Int, onTimeEntered: (Int) -> Unit) {
    var notificationTime by remember { mutableStateOf(time.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemTitle(label = "Notification Alert Time")

            Box(
                modifier = Modifier.width(64.dp).height(32.dp)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                BasicTextField(
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = TextFieldStyle,
                    value = notificationTime,
                    onValueChange = { input ->
                        notificationTime = input
                        errorMessage = validateNotificationTime(input)
                        if (errorMessage == null) {
                            onTimeEntered.invoke(input.toInt())
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                )
            }
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                style = DMSans18Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        ItemBody(label = "Set custom notification times in minutes. Enter 0 to receive a notification at the event start time (Premium feature)")
    }
}

private fun validateNotificationTime(input: String): String? {
    val time = input.toIntOrNull()
    return when {
        time == null -> "Please enter a valid number"
        time < 0 -> "We can't time travel yet. Time cannot be negative"
        time > 10080 -> "Time must be less than or equal to 7 days"
        else -> null // valid input
    }
}