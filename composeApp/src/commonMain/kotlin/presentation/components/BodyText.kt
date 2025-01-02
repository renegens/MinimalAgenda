package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import presentation.theme.DMSans24Medium60

@Composable
fun BodyText(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = DMSans24Medium60,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.tertiary,
    )
}