package presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import domain.model.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.PaperBackgroundColumn
import presentation.components.SettingsHeader
import presentation.theme.DMSans18Medium
import presentation.screens.settings.SettingsContract.*
import presentation.theme.TextFieldStyle

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .verticalScroll(state = rememberScrollState())
    ) {
        PaperBackgroundColumn(state.appTheme) {
            SettingsHeader("Settings")
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.isLoading) {
            return
        }

        PaperBackgroundColumn(state.appTheme) {
            SwitchSettingItem(title = "Private Notifications",
                isChecked = state.privateNotifications,
                onCheckedChange = { viewModel.setEvent(Event.OnTogglePrivateNotifications(it)) })
            ExplanationText(
                "When enabled, notifications will use a generic title.",
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        PaperBackgroundColumn(state.appTheme) {
            NotificationTimeInput(time = state.notificationTime, onTimeEntered = {
                viewModel.setEvent(Event.OnNotificationTimeEntered(it))
            })
            Spacer(modifier = Modifier.height(40.dp))
        }

        PaperBackgroundColumn(state.appTheme) {
            SettingsItem("Select your preferred mode", {})
            Spacer(modifier = Modifier.height(4.dp))
            ModeSelection(
                selectedTheme = state.appTheme
            ) { viewModel.setEvent(Event.OnModeSelected(it)) }
            Spacer(modifier = Modifier.height(40.dp))
        }

        PaperBackgroundColumn(state.appTheme) {
            SettingsItem("Don’t Miss Important Notifications", {})
            Spacer(modifier = Modifier.height(16.dp))
            ExplanationText("Open your Settings on iOS.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Go to Focus.")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Select the specific mode (e.g. Work, Do Not Disturb).")
            Spacer(modifier = Modifier.height(4.dp))
            ExplanationText("Under Allowed Notifications, add this app to ensure you receive important alerts.")
            Spacer(modifier = Modifier.height(64.dp))
        }

//        item {
//            SettingsItem(title = "Send feedback",
//                onClick = { viewModel.setEvent(Event.OnSentFeedback) })
//        }
    }
}

@Composable
private fun SettingsItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemText(label = title)
    }
}

@Composable
private fun SwitchSettingItem(
    title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ItemText(label = title)
        Switch(
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun ExplanationText(label: String) {
    Text(
        text = label,
        style = DMSans18Medium,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun ItemText(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text = label,
        style = DMSans18Medium,
        color = color,
    )
}

@Composable
private fun RadioButtonLabel(label: String, isSelected: Boolean) {
    Text(
        text = label,
        style = DMSans18Medium,
        fontSize = 16.sp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun ModeSelection(selectedTheme: AppTheme, onClick: (AppTheme) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppTheme.DefaultLight
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppTheme.DefaultLight) })
            RadioButtonLabel(label = "Light Mode", isSelected = isSelected)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppTheme.DefaultDark
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppTheme.DefaultDark) })
            RadioButtonLabel(label = "Dark Mode", isSelected = isSelected)

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val isSelected = selectedTheme == AppTheme.FollowSystem
            RadioButton(colors = RadioButtonDefaults.colors(unselectedColor = MaterialTheme.colorScheme.secondary),
                selected = isSelected,
                onClick = { onClick(AppTheme.FollowSystem) })
            RadioButtonLabel(label = "Follow System", isSelected = isSelected)
        }
    }
}

@Composable
fun NotificationTimeInput(time: Int, onTimeEntered: (Int) -> Unit) {
    var notificationTime by remember { mutableStateOf(time.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemText(label = "Notification Alert Time")

            Box(
                modifier = Modifier.width(64.dp).height(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ),
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
                    modifier = Modifier.fillMaxWidth(),
                )
            }

//
//            TextField(
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
//                ),
//                value = notificationTime,
//                onValueChange = { input ->
//                    notificationTime = input
//                    errorMessage = validateNotificationTime(input)
//                    if (errorMessage == null) {
//                        onTimeEntered.invoke(input.toInt())
//                    }
//                },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//                isError = errorMessage != null,
//                modifier = Modifier.width(88.dp)
//            )
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
        ExplanationText(label = "Set the notification time in minutes. Enter 0 to receive a notification at the event start time.")
    }
}

fun validateNotificationTime(input: String): String? {
    val time = input.toIntOrNull()
    return when {
        time == null -> "Please enter a valid number"
        time < 0 -> "We can't time travel yet. Time cannot be negative"
        time > 10080 -> "Time must be less than or equal to 7 days"
        else -> null // valid input
    }
}