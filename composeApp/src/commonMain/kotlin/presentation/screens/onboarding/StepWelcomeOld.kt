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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.APP_DISPLAY_NAME
import domain.model.AppThemeMode
import presentation.components.CalendarIcon
import presentation.components.MainCTAButton
import presentation.components.PaperBackground
import presentation.components.TextSubtitle
import presentation.theme.DMSans24Black100

//Not used here for backup
@Composable
fun StepWelcomeOld(onContinue: () -> Unit) {
    PaperBackground(appThemeMode = AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                style = DMSans24Black100,
                text = "Welcome to Your $APP_DISPLAY_NAME",
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextSubtitle(
                text = "Declutter Your Day,\nSimplify Your Life",
            )
            Spacer(modifier = Modifier.height(56.dp))
            CalendarIcon()
            Spacer(modifier = Modifier.weight(1F))
            MainCTAButton(
                label = "learn more",
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}