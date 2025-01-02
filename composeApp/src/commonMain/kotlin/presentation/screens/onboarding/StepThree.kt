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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.AppTheme
import domain.model.AppThemeMode
import presentation.components.MainButtonWholeWidth
import presentation.components.PaperBackground
import presentation.components.ParagraphDivider
import presentation.theme.CustomColor
import presentation.theme.DMSans24Medium120
import presentation.theme.DMSansBlack150

@Composable
fun StepThree(onContinue: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                style = DMSansBlack150,
                text = "Your Daily Planner helps you",
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.weight(0.5F))
            Text(
                style = DMSans24Medium120,
                text = "declutter your schedule",
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ParagraphDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                style = DMSans24Medium120,
                text = "experience a calm day",
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ParagraphDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                style = DMSans24Medium120.copy(fontWeight = FontWeight.SemiBold),
                text = "enjoy piece of mind",
                color = CustomColor.Orange_500,
            )
            Spacer(modifier = Modifier.weight(1F))
            MainButtonWholeWidth(
                label = "Let’s Begin",
                textStyle = DMSansBlack150.copy(fontSize = 20.sp, letterSpacing = (0.sp)),
                textColor = Color.White,
                buttonColor = AppTheme.Default.appColor.color,
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}