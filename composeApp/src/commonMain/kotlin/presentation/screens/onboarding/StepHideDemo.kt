package presentation.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.components.DemonstrationDay
import presentation.components.MainCTAButton
import presentation.components.TextSubtitle
import presentation.components.TextTitle

@Composable
fun StepHideDemo(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
    ) {
        TextTitle(text = "Keep Only What Matters")
        Spacer(modifier = Modifier.height(16.dp))
        TextSubtitle(text = "Tap to hide any event and focus on what is important")
        Spacer(modifier = Modifier.height(32.dp))
        DemonstrationDay()
        Spacer(modifier = Modifier.weight(1F))
        Spacer(modifier = Modifier.height(32.dp))
        MainCTAButton(
            label = "Learn How It Works",
            onClick = onContinue
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}