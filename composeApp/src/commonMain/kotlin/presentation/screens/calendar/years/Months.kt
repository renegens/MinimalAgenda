package presentation.screens.calendar.years

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaEvent
import domain.model.AgendaMonth
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.getEventsForMonth
import extensions.hasHiddenEvents
import extensions.today
import presentation.theme.DMSans24Black100
import presentation.theme.DMSans24Medium120

@Composable
fun Months(
    yearEvents: List<AgendaEvent>,
    showAll: Boolean,
    year: Int,
    onMonthClicked: (AgendaMonthYear) -> Unit
) {
    val months = AgendaMonth.entries.chunked(6)

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.weight(0.5f)) {
            months.first().forEachIndexed { index, month ->
                if (month.isCurrent(today, year)) {
                    MonthTextFocused(
                        text = month.nameRes,
                        modifier = Modifier.clickable {
                            onMonthClicked.invoke(
                                AgendaMonthYear(
                                    agendaYear = AgendaYear(year),
                                    agendaMonth = month
                                )
                            )
                        })
                } else {
                    val events = yearEvents.getEventsForMonth(month, year)
                    val hasMonthEvents = if (showAll) {
                        events.isNotEmpty()
                    } else {
                        events.hasHiddenEvents()
                    }
                    MonthText(
                        text = month.nameRes,
                        hasEvents = hasMonthEvents,
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

        Column(modifier = Modifier.weight(0.5f)) {
            months[1].forEachIndexed { index, month ->
                if (month.isCurrent(today, year)) {
                    MonthTextFocused(
                        text = month.nameRes,
                        modifier = Modifier.clickable {
                            onMonthClicked.invoke(
                                AgendaMonthYear(
                                    agendaYear = AgendaYear(year),
                                    agendaMonth = month
                                )
                            )
                        })
                } else {
                    val events = yearEvents.getEventsForMonth(month, year)
                    val hasMonthEvents = if (showAll) {
                        events.isNotEmpty()
                    } else {
                        events.hasHiddenEvents()
                    }
                    MonthText(
                        month.nameRes,
                        hasEvents = hasMonthEvents,
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
private fun MonthText(text: String, modifier: Modifier, hasEvents: Boolean) {
    val shortMonth = text.substring(0, 3)
    Box(Modifier.height(40.dp).fillMaxWidth().then(modifier)) {
        Text(
            text = shortMonth,
            style = DMSans24Medium120,
            color = if (hasEvents) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.wrapContentHeight(unbounded = true)
        )
    }

}

@Composable
private fun MonthTextFocused(text: String, modifier: Modifier) {
    Box(Modifier.height(40.dp).fillMaxWidth().then(modifier)) {
        Text(
            text,
            style = DMSans24Black100,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.wrapContentHeight(unbounded = true)
        )
    }
}