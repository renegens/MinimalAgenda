package presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.AgendaMonthYear
import domain.model.AgendaYear

@Composable
fun MonthHeader(monthYear: AgendaMonthYear, modifier: Modifier) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).then(modifier)) {
        val shortMonth = monthYear.agendaMonth.nameRes.substring(0,3)
        Text(
            text = shortMonth,
            style = MaterialTheme.typography.displayLarge,
            color = ColorPrimary(monthYear.isCurrent)
        )
        Text(
            text = monthYear.agendaYear.lastTwoDigits,
            style = MaterialTheme.typography.displayLarge,
            color = ColorSecondary(monthYear.isCurrent)
        )
    }
}

@Composable
fun DayHeader(day: String, dayInMonth: Int, month: Int, isToday: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = day,
            style = MaterialTheme.typography.displayLarge,
            color = ColorPrimary(isToday),
        )
        Text(
            "$dayInMonth.$month",
            style = MaterialTheme.typography.displayLarge,
            color = ColorSecondary(isToday)
        )
    }
}

@Composable
fun YearHeader(agendaYear: AgendaYear) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            agendaYear.firstTwoDigits,
            color = ColorSecondary(agendaYear.isCurrent),
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            agendaYear.lastTwoDigits,
            color = ColorPrimary(agendaYear.isCurrent),
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Composable
private fun ColorPrimary(isCurrent: Boolean): Color {
    return if (isCurrent) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

@Composable
private fun ColorSecondary(isCurrent: Boolean): Color {
    return if (isCurrent) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}