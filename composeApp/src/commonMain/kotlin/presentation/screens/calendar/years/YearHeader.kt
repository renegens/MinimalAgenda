package presentation.screens.calendar.years

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaYear

@Composable
fun YearHeader(agendaYear: AgendaYear) {
    val currentYearColor = if (agendaYear.isCurrent) {
        MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondary
    }

    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            agendaYear.firstTwoDigits,
            color = currentYearColor.first,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            agendaYear.lastTwoDigits,
            color = currentYearColor.second,
            style = MaterialTheme.typography.displayLarge
        )
    }
}