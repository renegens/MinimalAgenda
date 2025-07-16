package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import minimalagenda.composeapp.generated.resources.Res
import minimalagenda.composeapp.generated.resources.tex_dark
import minimalagenda.composeapp.generated.resources.tex_light
import org.jetbrains.compose.resources.painterResource
import presentation.theme.InvertedAppColors

@Composable
fun HideToolTip(openPaywall: () -> Unit) {
    Box(modifier = Modifier.wrapContentSize()) {
        val res = if (isSystemInDarkTheme()) {
            Res.drawable.tex_light
        } else {
            Res.drawable.tex_dark
        }

        Image(
            painter = painterResource(res),
            contentDescription = "paper_texture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        InvertedAppColors {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(16.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 128.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextBodyRegular(
                    "Hide any event by tapping it",
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextBodyRegular(
                    "View hidden events by tapping 'All'",
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextBodyRegular(
                    "Free plan: Hide up to 5 events\nPremium: Unlimited event hiding",
                )
                Spacer(modifier = Modifier.height(24.dp))
                MainCTAButton("Upgrade to Premium", openPaywall)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}