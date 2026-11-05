package presentation.screens.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import presentation.theme.iosBody
import presentation.theme.iosHeadline
import presentation.theme.iosTitle3

@Composable
internal fun ItemBody(label: String) {
    Text(
        text = label,
        style = iosBody,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
internal fun ItemTitle(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text = label,
        style = iosTitle3.copy(fontWeight = FontWeight.SemiBold),
        color = color,
    )
}

@Composable
internal fun ItemHeadline(label: String, color: Color = MaterialTheme.colorScheme.tertiary) {
    Text(
        text = label,
        style = iosHeadline,
        color = color,
    )
}