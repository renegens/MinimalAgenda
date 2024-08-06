package presentation.components.years

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import domain.model.AgendaYear

@Composable
fun YearHeader(year: AgendaYear, isCurrentYear: Boolean) {
    val currentYearColor = if (isCurrentYear) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row {
        Text(
            year.firstPart,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            year.secondPart,
            color = currentYearColor,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}