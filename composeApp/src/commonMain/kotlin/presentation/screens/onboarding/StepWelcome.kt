package presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.APP_DISPLAY_NAME
import domain.model.AppTheme
import domain.model.AppThemeMode
import presentation.components.CalendarIcon
import presentation.components.MainButtonWholeWidth
import presentation.components.PaperBackground
import presentation.theme.DMSansBlack250

//Not used here for backup
@Composable
fun StepWelcome(onContinue: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                style = DMSansBlack250,
                text = "Welcome to Your $APP_DISPLAY_NAME",
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(56.dp))
            CalendarIcon()
            Spacer(modifier = Modifier.weight(1F))
            MainButtonWholeWidth(
                label = "continue",
                buttonColor = AppTheme.Default.appColor.color,
                textColor = Color.White,
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}