package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.APP_DISPLAY_NAME
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.CalendarBadgePlus
import io.github.alexzhirkevich.cupertino.icons.outlined.CheckmarkCircle
import io.github.alexzhirkevich.cupertino.icons.outlined.Gear
import io.github.alexzhirkevich.cupertino.icons.outlined.Lock
import platform.CalendarPermissionHandler.State
import presentation.theme.DMSans18Medium
import presentation.theme.DMSansBlack36

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.CalendarPermission(
    onPermissionRequest: () -> Unit,
    state: State
) {
    when (state) {
        State.FullAccess -> {}
        State.Denied -> stickyHeader { AskForPermission(onPermissionRequest, state) }
        State.NotDetermined -> stickyHeader { AskForPermission(onPermissionRequest, state) }
    }
}

@Composable
private fun AskForPermission(onPermissionRequest: () -> Unit, state: State) {
    Box(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp).shadow(elevation = 8.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(color = MaterialTheme.colorScheme.inversePrimary)
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Calendar Access",
                modifier = Modifier.fillMaxWidth(),
                style = DMSans18Medium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.inverseSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (state == State.Denied) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.CalendarBadgePlus,
                        contentDescription = "calendar",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        BodyText(
                            "This app needs calendar permission to work. Please go to Settings -> Apps -> $APP_DISPLAY_NAME and turn on Calendars",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BodyText(
                            "Learn more in the ‘What Happens with Your Data’ section of Daily Planner settings",
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))

            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.CalendarBadgePlus,
                        contentDescription = "calendar",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BodyText(
                        "$APP_DISPLAY_NAME integrates seamlessly with any calendar on your device"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(4.dp))
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.Lock,
                        contentDescription = "lock",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    BodyText("All your data stays private, securely stored locally on your device, and is never accessed by third parties")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.Gear,
                        contentDescription = "gear",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BodyText("You can revoke calendar access at any time in the iOS Settings app under Privacy -> Calendars")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.CheckmarkCircle,
                        contentDescription = "checkmark circle",
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BodyText("Swipe left to access the settings page and learn more about how your data is handled")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onPermissionRequest,
                    modifier = Modifier.wrapContentWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
                ) {
                    Text(
                        "CONTINUE",
                        style = DMSansBlack36,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BodyText(label: String) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        style = DMSans18Medium.copy(fontSize = 16.sp),
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.inverseSurface
    )
}