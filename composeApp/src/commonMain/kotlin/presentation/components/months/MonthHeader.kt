package presentation.components.months

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MonthHeader(month: String, year: Int, color: Color, isCurrentMonth: Boolean) {

    val color = if (isCurrentMonth){
        Color.Yellow
    }else{
        color
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = month,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
        Text(
            text = year.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
        Divider(color = color)
    }
}