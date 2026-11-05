package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ParagraphDivider(widthFraction: Float = 0.5F) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(widthFraction),
        color = MaterialTheme.colorScheme.secondary
    )
}