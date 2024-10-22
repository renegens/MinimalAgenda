package presentation.screens.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import presentation.theme.iosBody
import presentation.theme.iosTitle3

@Composable
internal fun ExplanationText(label: String) {
    Text(
        text = label,
        style = iosBody,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
internal fun ItemText(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text = label,
        style = iosTitle3.copy(fontWeight = FontWeight.SemiBold),
        color = color,
    )
}