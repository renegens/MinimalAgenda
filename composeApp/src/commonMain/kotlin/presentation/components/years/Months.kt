package presentation.components.years

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import data.Months
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun Months(renderingYear: Int, today: LocalDate) {
    val months = Months.entries.chunked(6)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(0.5f)) {
            months.first().forEachIndexed { index, month ->
                val isCurrent =
                    month.ordinal + 1 == today.month.number && today.year == renderingYear
                if (isCurrent) {
                    MonthTextFocused(month.name)
                } else {
                    MonthText(month.nameRes)
                }
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            months[1].forEachIndexed { index, month ->
                val isCurrent =
                    month.ordinal + 1 == today.month.number && today.year == renderingYear
                if (isCurrent) {
                    MonthTextFocused(month.name)
                } else {
                    MonthText(month.nameRes)
                }
            }
        }
    }
}

@Composable
private fun MonthText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun MonthTextFocused(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.tertiary
    )
}