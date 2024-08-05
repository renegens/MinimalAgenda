package presentation.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SectionDivider(isCurrent: Boolean) {
    val current = if (isCurrent) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }
    HorizontalDivider(color = current)
}

