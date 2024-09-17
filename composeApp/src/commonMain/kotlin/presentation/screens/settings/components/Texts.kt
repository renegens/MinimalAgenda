package presentation.screens.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import presentation.theme.DMSans18Medium

@Composable
internal fun ExplanationText(label: String) {
    Text(
        text = label,
        style = DMSans18Medium,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
internal fun ItemText(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text = label,
        style = DMSans18Medium,
        color = color,
    )
}