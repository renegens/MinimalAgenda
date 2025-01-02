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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.APP_DISPLAY_NAME
import platform.CalendarPermissionHandler.State
import presentation.theme.AppCornerShape
import presentation.theme.CustomColor
import presentation.theme.DMSansBlack150
import presentation.theme.InvertedAppTheme

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
            .clip(AppCornerShape)
    ) {
        InvertedAppTheme {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(AppCornerShape)
                    .padding(16.dp),
            ) {
                when (state){
                    State.FullAccess -> {}
                    State.Denied -> Denied()
                    State.NotDetermined -> NotDetermined(onPermissionRequest)
                }
            }
        }
    }
}

@Composable
private fun Denied() {
    BodyText(
        "$APP_DISPLAY_NAME needs calendar access. Go to Settings > Apps > $APP_DISPLAY_NAME and enable Calendars.",
    )
    Spacer(modifier = Modifier.height(16.dp))
    ParagraphDivider()
    Spacer(modifier = Modifier.height(16.dp))
    BodyText(
        "See ‘What Happens with Your Data’ in $APP_DISPLAY_NAME settings.",
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun NotDetermined(onPermissionRequest: () -> Unit) {
    Text(
        text = "Calendar Access",
        modifier = Modifier.fillMaxWidth(),
        style = DMSansBlack150.copy(fontSize = 40.sp),
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.tertiary,
    )
    Spacer(modifier = Modifier.height(24.dp))
    BodyText("$APP_DISPLAY_NAME seamlessly integrates with your device’s calendar.")
    Spacer(modifier = Modifier.height(16.dp))
    ParagraphDivider()
    Spacer(modifier = Modifier.height(16.dp))
    BodyText("Your data stays private, stored locally, and never shared with third parties.")
    Spacer(modifier = Modifier.height(16.dp))
    ParagraphDivider()
    Spacer(modifier = Modifier.height(16.dp))
    BodyText("Revoke calendar access anytime in iOS Settings > Privacy > Calendars.")
    Spacer(modifier = Modifier.height(24.dp))
    MainButtonWholeWidth(
        label = "continue",
        buttonColor = MaterialTheme.colorScheme.primary,
        textColor = CustomColor.White_500,
        onClick = onPermissionRequest
    )
    Spacer(modifier = Modifier.height(16.dp))
}