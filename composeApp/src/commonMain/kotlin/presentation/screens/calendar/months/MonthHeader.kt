package presentation.screens.calendar.months

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.AgendaMonth
import domain.model.AgendaYear
import kotlinx.datetime.Month

@Composable
fun MonthHeader(month: AgendaMonth, year: Int, isCurrentMonth: Boolean, modifier: Modifier) {

    val color = if (isCurrentMonth) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).then(modifier)) {
        Text(
            text = month.nameRes,
            style = MaterialTheme.typography.headlineLarge,
            color = color
        )
        Text(
            text = year.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}