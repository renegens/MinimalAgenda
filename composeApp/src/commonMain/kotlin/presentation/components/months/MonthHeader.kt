package presentation.components.months

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.MonthData

@Composable
fun MonthHeader(monthData: MonthData, isCurrentMonth: Boolean) {

    val color = if (isCurrentMonth) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = monthData.shortMonth,
            style = MaterialTheme.typography.headlineLarge,
            color = color
        )
        Text(
            text = monthData.shortYear,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}