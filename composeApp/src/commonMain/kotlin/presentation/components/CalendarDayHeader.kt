package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import platform.CalendarPermissionHandler.State
import presentation.theme.AppCornerShape
import presentation.theme.InvertedAppTheme

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.CalendarDayHeader(
    onPermissionRequest: () -> Unit,
    openSettings: () -> Unit,
    state: State,
) {
    when (state) {
        State.FullAccess -> {}
        State.Denied -> stickyHeader { AskForPermission(onPermissionRequest, openSettings, state) }
        State.NotDetermined -> stickyHeader {
            AskForPermission(
                onPermissionRequest, openSettings, state
            )
        }
    }
}

@Composable
private fun AskForPermission(
    onPermissionRequest: () -> Unit,
    openSettings: () -> Unit,
    state: State,
) {
    Box(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp).shadow(elevation = 8.dp)
            .clip(AppCornerShape)
    ) {
        InvertedAppTheme {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().clip(AppCornerShape)
                    .padding(16.dp),
            ) {
                when (state) {
                    State.FullAccess -> {}
                    State.Denied -> Denied(openSettings = openSettings)
                    State.NotDetermined -> NotDetermined(onPermissionRequest)
                }
            }
        }
    }
}

@Composable
private fun Denied(openSettings: () -> Unit) {
    TextTitleSmall(
        "Permissions Denied",
    )
    Spacer(modifier = Modifier.height(16.dp))
    TextBodyRegular(
        "You’re exploring with demo data. To declutter your real schedule, grant calendar access in your device settings",
    )
    Spacer(modifier = Modifier.height(24.dp))
    MainCTAButton("Go to settings", openSettings)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun NotDetermined(onPermissionRequest: () -> Unit) {
    TextTitleSmall(text = "Explore Decluttering in Action")
    Spacer(modifier = Modifier.height(16.dp))
    TextBodyRegular("Tap to hide events, or press All to view hidden ones. Connect your calendar for the full experience.")
    Spacer(modifier = Modifier.height(24.dp))
    MainCTAButton("continue", onPermissionRequest)
    Spacer(modifier = Modifier.height(16.dp))
}