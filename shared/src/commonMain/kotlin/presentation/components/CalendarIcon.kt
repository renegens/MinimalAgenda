package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import extensions.today
import presentation.theme.AppCornerShape
import presentation.theme.CustomColor
import presentation.theme.DMSans24Black100

@Composable
fun CalendarIcon() {
    val color = if (isSystemInDarkTheme()) {
        CustomColor.APP_ICON_BACKGROUND_DARK
    } else {
        CustomColor.APP_ICON_BACKGROUND_LIGHT
    }

        Box(
            modifier = Modifier
                .size(140.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                .background(
                    color = color,
                    shape = AppCornerShape,
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = today.day.toString(),
                    style = DMSans24Black100,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(40.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = today.month.name.substring(0,3),
                    style = DMSans24Black100.copy(fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
            }

    }
}