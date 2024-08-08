package presentation.screens.calendar.months

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaMonthYear

@Composable
fun MonthHeader(monthYear: AgendaMonthYear, modifier: Modifier) {

    val color = if (monthYear.isCurrent) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).then(modifier)) {
        Text(
            text = monthYear.agendaMonth.nameRes,
            style = MaterialTheme.typography.displayLarge,
            color = color
        )
        Text(
            text = monthYear.agendaYear.lastTwoDigits,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}