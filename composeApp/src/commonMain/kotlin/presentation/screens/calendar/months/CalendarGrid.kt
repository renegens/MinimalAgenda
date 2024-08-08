package presentation.screens.calendar.months

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import domain.model.DayGrid
import domain.model.createGridDay
import extensions.today
import presentation.components.SectionDivider

@Composable
fun MonthGrid(
    monthYear: AgendaMonthYear,
    onDayClicked: (Int) -> Unit,
    onYearClicked: (AgendaYear) -> Unit
) {
    val isCurrent by remember { mutableStateOf(monthYear.isCurrent) }

    Spacer(modifier = Modifier.height(16.dp))
    MonthHeader(
        monthYear = monthYear,
        modifier = Modifier.clickable { onYearClicked.invoke(monthYear.agendaYear) })
    Spacer(modifier = Modifier.height(16.dp))
    DaysInWeek(today = today, isCurrentMonth = isCurrent)
    CalendarDays(
        monthYear = monthYear,
        onDayClicked = onDayClicked
    )
    Spacer(modifier = Modifier.height(16.dp))
    SectionDivider(isCurrent = isCurrent)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(
    monthYear: AgendaMonthYear,
    onDayClicked: (Int) -> Unit
) {
    val gridDays by derivedStateOf {
        createGridDay(monthYear)
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        maxItemsInEachRow = 7
    ) {
        gridDays.forEach { gridItem ->

            val hasToday by derivedStateOf {
                gridItem.epochDay == today.toEpochDays()
            }

            if (gridItem.dayInMonth == DayGrid.DONT_RENDER) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(44.dp).width(44.dp),
                    color = Color.Transparent
                )
            } else {
                TextButton(
                    onClick = { onDayClicked.invoke(gridItem.epochDay) },
                    modifier = Modifier.height(44.dp).width(44.dp)
                ) {
                    Text(
                        text = gridItem.dayInMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        color = if (hasToday) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}