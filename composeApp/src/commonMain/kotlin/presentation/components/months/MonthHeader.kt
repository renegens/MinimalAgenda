package presentation.components.months

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import presentation.theme.Header1
import presentation.theme.Header2

@Composable
fun MonthHeader(month: String, year: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = month,
            style = Header1,
            color = color
        )
        Text(
            text = year.toString(),
            style = Header2,
            color = color
        )
        Divider(color = color)
    }
}