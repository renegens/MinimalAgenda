package presentation.screens.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AppTheme
import domain.model.AppThemeMode
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.day_view_dark
import org.jetbrains.compose.resources.painterResource
import presentation.components.BodyText
import presentation.components.MainButtonWholeWidth
import presentation.components.PaperBackground
import presentation.theme.DMSansBlack150

@Composable
fun StepTwo(onContinue: () -> Unit) {
    PaperBackground(AppThemeMode.FollowSystem) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                style = DMSansBlack150,
                text = "Your Day, Simplified",
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            BodyText(text = "Effortlessly track all your events and activities in one beautifully organized view")
            Spacer(modifier = Modifier.weight(1F))
            Image(
                modifier = Modifier.height(400.dp).align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(Res.drawable.day_view_dark),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
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