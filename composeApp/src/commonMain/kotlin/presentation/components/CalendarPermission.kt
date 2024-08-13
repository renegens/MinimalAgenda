package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import platform.CalendarPermissionHandler.State

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.CalendarPermission(
    onPermissionRequest: () -> Unit,
    state: State
) {
    when (state) {
        State.FullAccess -> {}
        State.Denied -> {}
        State.NotDetermined -> stickyHeader { AskForPermission(onPermissionRequest) }
    }
}

@Composable
private fun AskForPermission(onPermissionRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "We need access to your calendar to provide the best experience.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onPermissionRequest,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Grant Calendar Permission")
        }
    }
}