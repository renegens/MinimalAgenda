package presentation.components.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import presentation.theme.Header2

@Composable
fun DaysInWeek() {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(modifier = Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.SpaceAround) {
        daysOfWeek.forEach {
            Text(
                modifier = Modifier.width(44.dp),
                text = it,
                style = Header2,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
        }
    }
}