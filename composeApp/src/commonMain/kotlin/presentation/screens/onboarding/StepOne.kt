package presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.day_view_dark
import org.jetbrains.compose.resources.painterResource
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import presentation.components.ParagraphDivider
import presentation.components.Subtitle
import presentation.theme.DMSansBlack120

@Composable
fun StepOne(onContinue: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        ) {
            Text(
                style = DMSansBlack120,
                text = "Declutter Your Day,\nSimplify Your Life",
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            Subtitle(text = "• Your calendar, reimagined.")
            ParagraphDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Subtitle(text = "• Hide optional meetings.")
            ParagraphDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Subtitle(text = "• Your data stays secure.")
            ParagraphDivider()
            Spacer(modifier = Modifier.weight(0.8F))
            Image(
                modifier = Modifier.height(400.dp).align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(Res.drawable.day_view_dark),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.weight(1F))
            MainCTAButton("Learn More", onContinue)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}