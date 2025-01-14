package presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import presentation.components.DemonstrationDay
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import presentation.components.ParagraphDivider
import presentation.components.Subtitle
import presentation.theme.DMSansBlack120

@Composable
fun StepTwo(onContinue: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        ) {
            Text(
                style = DMSansBlack120,
                text = "Hide What You Don’t Need",
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            Subtitle(text = "Easily hide optional meetings to declutter your schedule in seconds")
            //Spacer(modifier = Modifier.weight(0.7F))
            Spacer(modifier = Modifier.height(16.dp))
            DemonstrationDay()
            Spacer(modifier = Modifier.weight(1F))
            ParagraphDivider(widthFraction = 1F)
            Spacer(modifier = Modifier.height(32.dp))
            MainCTAButton(
                label = "Learn How It Works",
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}