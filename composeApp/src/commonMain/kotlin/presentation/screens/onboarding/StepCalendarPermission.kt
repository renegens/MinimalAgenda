package presentation.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.filled.LockCircle
import io.github.alexzhirkevich.cupertino.icons.outlined.CalendarBadgePlus
import platform.CalendarPermissionHandler
import presentation.components.MainButtonWholeWidth
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import presentation.components.TextSubtitle
import presentation.components.TextTitle
import presentation.theme.CustomColor
import presentation.theme.DMSans24Medium60

@Composable
fun StepCalendarPermission(
    calendarPermissionState: CalendarPermissionHandler.State,
    onContinue: () -> Unit,
    requestCalendarPermission: () -> Unit,
    openSettings: () -> Unit
) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            if (calendarPermissionState == CalendarPermissionHandler.State.Denied) {
                PermissionDenied(onContinue, openSettings)
            } else {
                AskingForPermission(onContinue, requestCalendarPermission)
            }
        }
    }
}

@Composable
private fun ColumnScope.PermissionDenied(
    onContinue: () -> Unit,
    openSettings: () -> Unit
) {
    TextTitle(text = "Permission Denied")
    Spacer(modifier = Modifier.height(16.dp))
    TextSubtitle(text = "We can’t sync your calendar without access.")
    Spacer(modifier = Modifier.weight(1F))

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = CupertinoIcons.Outlined.CalendarBadgePlus,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(90.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Enable calendar access in iOS settings to display your events.",
            modifier = Modifier.fillMaxWidth(),
            style = DMSans24Medium60,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your data is private and secure.",
            modifier = Modifier.fillMaxWidth(),
            style = DMSans24Medium60,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
        )

    }
    Spacer(modifier = Modifier.weight(1F))

    MainCTAButton("try demo", onContinue)
    Spacer(modifier = Modifier.height(48.dp))

    MainButtonWholeWidth(
        label = "Go to Settings",
        onClick = openSettings,
        buttonColor = MaterialTheme.colorScheme.tertiary,
        textColor = MaterialTheme.colorScheme.surface
    )
    Spacer(modifier = Modifier.height(16.dp))

}

@Composable
private fun ColumnScope.AskingForPermission(
    onContinue: () -> Unit,
    requestCalendarPermission: () -> Unit
) {
    TextTitle(text = "Calendar Permission")
    Spacer(modifier = Modifier.height(16.dp))
    TextSubtitle(text = "Sync with your calendar to simplify planning—no duplicates, no stress.")
    Spacer(modifier = Modifier.weight(1F))
    Icon(
        imageVector = CupertinoIcons.Filled.LockCircle,
        contentDescription = null,
        tint = CustomColor.Orange_700,
        modifier = Modifier.size(90.dp).align(Alignment.CenterHorizontally),
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = "We don’t store or share your information.",
            modifier = Modifier.fillMaxWidth(),
            style = DMSans24Medium60,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
        )

    }
    Spacer(modifier = Modifier.weight(1F))

    MainCTAButton("continue", requestCalendarPermission)
    Spacer(modifier = Modifier.height(48.dp))

    MainButtonWholeWidth(
        label = "Try demo",
        onClick = onContinue,
        buttonColor = MaterialTheme.colorScheme.tertiary,
        textColor = MaterialTheme.colorScheme.surface
    )
    Spacer(modifier = Modifier.height(16.dp))
}