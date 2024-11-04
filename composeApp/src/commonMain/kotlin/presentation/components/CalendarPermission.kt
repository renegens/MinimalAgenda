package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import platform.CalendarPermissionHandler.State
import presentation.theme.iosBody
import presentation.theme.iosHeadline

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
    Box(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val bodyText = when (state) {
                State.NotDetermined -> {
                    "To make scheduling easier, we need access to your calendar. This lets you view and add events directly from the app."
                }

                State.Denied -> {
                    "To access calendar features, please enable calendar permissions in your device settings."
                }

                else -> {
                    ""
                }
            }

            Text(
                text = bodyText,
                modifier = Modifier.fillMaxWidth(),
                style = iosBody,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.tertiary
            )

            when (state) {
                State.NotDetermined -> {
                    Button(
                        onClick = onPermissionRequest,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
                    ) {
                        Text(
                            "Continue",
                            style = iosHeadline,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                }

                State.Denied -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Go to Settings > Apps > Daily Planner > Calendar, then turn on calendar access.",
                        modifier = Modifier.fillMaxWidth(),
                        style = iosBody,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                else -> {}
            }
        }
    }
}