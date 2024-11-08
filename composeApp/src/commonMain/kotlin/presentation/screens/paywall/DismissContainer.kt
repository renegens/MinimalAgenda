package presentation.screens.paywall

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import presentation.theme.iosBody
import presentation.theme.iosFootnote
import presentation.theme.iosSubheadline

@Composable
fun DismissContainer(isProcessing: Boolean, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Made by one person with no investors or corporate owner",
            style = iosSubheadline,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Not interested?",
            style = iosFootnote,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Dismiss",
            style = iosBody.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable {
                if (isProcessing.not()) {
                    onDismiss.invoke()
                }
            },
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inversePrimary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "No payment now", style = iosBody, color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "How can I cancel subscription?",
            style = iosFootnote.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "It's easy: Open the app, tap Settings and then scroll down to Cancel subscription",
            style = iosFootnote,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface

        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "You're in your App Store subscriptions. Select Daily Planner press Cancel Trial and confirm.",
            style = iosFootnote,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}