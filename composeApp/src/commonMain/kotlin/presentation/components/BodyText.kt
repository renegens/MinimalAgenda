package presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import presentation.theme.DMSans24Light100
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

@Composable
fun BodyTextBold(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = DMSans24Medium60.copy(fontWeight = FontWeight.ExtraBold),
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
fun BodyTextBoldLight(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = DMSans24Medium60.copy(fontWeight = FontWeight.Light, fontStyle = FontStyle.Italic),
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = DMSans24Light100,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.tertiary,
    )
}