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
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.filled.LockCircle
import com.slapps.cupertino.icons.outlined.CalendarBadgePlus
import platform.CalendarPermissionHandler
import presentation.components.BodyText
import presentation.components.ButtonWholeWidth
import presentation.components.MainCTAButton
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
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        if (calendarPermissionState == CalendarPermissionHandler.State.Denied) {
            PermissionDenied(onContinue, openSettings)
        } else {
            AskingForPermission(onContinue, requestCalendarPermission)
        }
    }
}

@Composable
private fun ColumnScope.PermissionDenied(
    onContinue: () -> Unit,
    openSettings: () -> Unit
) {
    TextTitle(text = "Calendar Access Required")
    Spacer(modifier = Modifier.height(16.dp))
    TextSubtitle(text = "To hide your real events, we need access to your calendar.")
    Spacer(modifier = Modifier.weight(1F))

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = CupertinoIcons.Outlined.CalendarBadgePlus,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(90.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        BodyText(
            text = "Please enable calendar access in Settings.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    Spacer(modifier = Modifier.weight(1F))

    MainCTAButton("Use with Demo Data", onContinue)
    Spacer(modifier = Modifier.height(48.dp))

    ButtonWholeWidth(
        label = "Open Settings",
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
    TextTitle(text = "Connect Your Calendar")
    Spacer(modifier = Modifier.height(16.dp))
    TextSubtitle(text = "This app works directly with your phone's built-in calendar database.")
    Spacer(Modifier.weight(1.0f))
    Icon(
        imageVector = CupertinoIcons.Filled.LockCircle,
        contentDescription = null,
        tint = CustomColor.Orange_700,
        modifier = Modifier.size(90.dp).align(Alignment.CenterHorizontally),
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = "Your calendar data stays on your device. We never access, store, or share your personal information.",
            modifier = Modifier.fillMaxWidth(),
            style = DMSans24Medium60,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
        )

    }
    Spacer(modifier = Modifier.weight(1F))

    MainCTAButton("Grant Access", requestCalendarPermission)
    Spacer(modifier = Modifier.height(48.dp))

    ButtonWholeWidth(
        label = "Continue without access",
        onClick = onContinue,
        buttonColor = MaterialTheme.colorScheme.tertiary,
        textColor = MaterialTheme.colorScheme.surface
    )
    Spacer(modifier = Modifier.height(16.dp))
}