package presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AppThemeMode
import presentation.components.BodyText
import presentation.components.BodyTextBold
import presentation.components.BodyTextBoldLight
import presentation.components.MainButtonWholeWidth
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import presentation.components.ParagraphDivider
import presentation.components.Subtitle
import presentation.theme.DMSansBlack120

@Composable
fun StepThree(onContinue: () -> Unit, requestCalendarPermission: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                style = DMSansBlack120,
                text = "Take Control of Your Schedule",
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))
            Subtitle(text = "We work with your existing calendar to simplify your planning. No duplicates, no hassle—just clarity.")
            Spacer(modifier = Modifier.height(16.dp))
            ParagraphDivider()
//            Icon(
//                tint = MaterialTheme.colorScheme.tertiary,
//                imageVector = Icons.Default.Lock,
//                contentDescription = "A Sample Check Icon",
//                modifier = Modifier.width(150.dp).height(150.dp).align(Alignment.CenterHorizontally)
//            )

            //Calendar
            Spacer(modifier = Modifier.height(16.dp))
            BodyText(text = "To declutter your real schedule, we need access to your calendar.")
            Spacer(modifier = Modifier.height(16.dp))
            BodyTextBold(text = "Your data stays on your device.")
            Spacer(modifier = Modifier.height(16.dp))
            BodyTextBold(text = "We don’t store or share your information.")
            //Spacer(modifier = Modifier.height(32.dp))
            //BodyText(text = "Not ready yet? Start with sample data and explore how we simplify your day.")
            Spacer(modifier = Modifier.weight(1F))
            MainCTAButton("continue", requestCalendarPermission)
            Spacer(modifier = Modifier.height(54.dp))
            MainButtonWholeWidth(label = "Try sample data", onClick = onContinue, buttonColor = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.surface)
            Spacer(modifier = Modifier.height(8.dp))
            BodyTextBoldLight(text = "Preview the app with sample events—no data required.")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}