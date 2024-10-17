package presentation.screens.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import presentation.theme.CustomColor
import presentation.theme.iosBody
import presentation.theme.iosTitle3

@Composable
private fun Step(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = iosTitle3.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.inversePrimary,
            )
            Text(
                text = description,
                style = iosBody,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@Composable
fun SubscriptionsSteps() {
    Column(
        modifier = Modifier.padding(start = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Step(
            title = "Today",
            description = "Get instant access to all features and get back control of your time",
            icon = Icons.Default.Lock,
            iconColor = MaterialTheme.colorScheme.inversePrimary,
        )
        Step(
            title = "Day 5",
            description = "You will get a notification that your trial is ending",
            icon = Icons.Default.Notifications,
            iconColor = MaterialTheme.colorScheme.inversePrimary
        )
        Step(
            title = "Day 7",
            description = "Your subscription starts, cancel anytime before",
            icon = Icons.Default.CheckCircle,
            iconColor = CustomColor.Mint_500
        )
    }
}