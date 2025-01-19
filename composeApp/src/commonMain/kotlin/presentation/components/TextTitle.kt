package presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import presentation.theme.DMSansBlack100
import presentation.theme.DMSansBlack60

@Composable
fun TextTitle(modifier: Modifier = Modifier, text: String) {
    Text(
        style = DMSansBlack100,
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Start
    )
}

@Composable
fun TextTitleSmall(text: String) {
    Text(
        style = DMSansBlack60,
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Start
    )
}