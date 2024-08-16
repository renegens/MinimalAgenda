package presentation.screens.calendar.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.DayInWeek
import kotlinx.datetime.LocalDate

@Composable
fun DaysInWeek(today: LocalDate, isCurrentMonth: Boolean) {
    val todayIndex = today.dayOfWeek.ordinal

    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DayInWeek.entries.forEachIndexed { index, daysInWeek ->
            Text(
                modifier = Modifier.width(44.dp),
                text = daysInWeek.firstLetter,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (index == todayIndex && isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            )
        }
    }
}