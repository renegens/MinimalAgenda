package presentation.screens.calendar.years

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaMonth
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.today
import kotlinx.datetime.number

@Composable
fun Months(year: Int, onMonthClicked: (AgendaMonthYear) -> Unit) {
    val months = AgendaMonth.entries.chunked(6)

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.weight(0.5f)) {
            months.first().forEachIndexed { _, month ->
                val isCurrent =
                    month.number == today.month.number && today.year == year
                if (isCurrent) {
                    MonthTextFocused(month.name, modifier = Modifier.clickable {
                        onMonthClicked.invoke(
                            AgendaMonthYear(
                                agendaYear = AgendaYear(year),
                                agendaMonth = month
                            )
                        )
                    })
                } else {
                    MonthText(month.nameRes, modifier = Modifier.clickable {
                        onMonthClicked.invoke(
                            AgendaMonthYear(
                                agendaYear = AgendaYear(year),
                                agendaMonth = month
                            )
                        )
                    })
                }
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            months[1].forEachIndexed { _, month ->
                val isCurrent =
                    month.number == today.month.number && today.year == year
                if (isCurrent) {
                    MonthTextFocused(month.name, modifier = Modifier.clickable {
                        onMonthClicked.invoke(
                            AgendaMonthYear(
                                agendaYear = AgendaYear(year),
                                agendaMonth = month
                            )
                        )
                    })
                } else {
                    MonthText(
                        month.nameRes,
                        modifier = Modifier.clickable {
                            onMonthClicked.invoke(
                                AgendaMonthYear(
                                    agendaYear = AgendaYear(year),
                                    agendaMonth = month
                                )
                            )
                        })
                }
            }
        }
    }
}

@Composable
private fun MonthText(text: String, modifier: Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.then(modifier)
    )
}

@Composable
private fun MonthTextFocused(text: String, modifier: Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.then(modifier)
    )
}