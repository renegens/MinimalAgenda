package presentation.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import presentation.components.TextTitle

@Composable
fun StepWelcome(onContinue: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(0.5F))
            TextTitle(text = "Welcome,\nYour All in One $APP_DISPLAY_NAME")
            Spacer(modifier = Modifier.height(16.dp))
            TextSubtitle("Simplify your schedule\nMake the most of your day")
            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CalendarIcon()
            }
            Spacer(modifier = Modifier.weight(1F))

            MainCTAButton(
                label = "Start now",
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(16.dp))

    }
}