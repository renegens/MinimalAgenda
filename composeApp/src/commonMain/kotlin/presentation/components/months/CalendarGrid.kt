package presentation.components.months

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
import domain.model.AgendaDay
import domain.model.AgendaMonth
import domain.model.GridDay
import domain.model.createGridDay
import extensions.uniqueMonthID
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import presentation.components.SectionDivider

@Composable
fun MonthGrid(
    days: List<AgendaDay>,
    month: Month,
    year: Int,
    today: LocalDate,
    onDayClicked: (Int) -> Unit
) {
    val isCurrent by derivedStateOf { today.month == month && today.year == year }

    Spacer(modifier = Modifier.height(16.dp))
    MonthHeader(month = month, year = year, isCurrentMonth = isCurrent)
    Spacer(modifier = Modifier.height(16.dp))
    DaysInWeek(today = today, isCurrentMonth = isCurrent)
    CalendarDays(
        month = month,
        year = year,
        days = days,
        today = today,
        onDayClicked = onDayClicked
    )
    Spacer(modifier = Modifier.height(16.dp))
    SectionDivider(isCurrent = isCurrent)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(agendaMonth: AgendaMonth, today: LocalDate, onDayClicked: () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        maxItemsInEachRow = 7
    ) {
        var selectedDay: Int? = null

        agendaMonth.days.forEachIndexed { index, i ->
            val day = agendaMonth.days[index]
            if (agendaMonth.id == today.uniqueMonthID()) {
                if (today.dayOfMonth == day) {
                    selectedDay = today.dayOfMonth
                } else {
                    selectedDay = null
                }
            }

            if (day == 0) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(44.dp).width(44.dp),
                    color = Color.Transparent
                )
            } else {
                TextButton(
                    onClick = { onDayClicked.invoke() },
                    modifier = Modifier.height(44.dp).width(44.dp)
                ) {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        color = if (selectedDay == day) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(
    days: List<AgendaDay>,
    month: Month,
    year: Int,
    today: LocalDate,
    onDayClicked: (Int) -> Unit
) {
    val gridDays by derivedStateOf {
        createGridDay(year = year, month = month)
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

            if (gridItem.dayInMonth == GridDay.DONT_RENDER) {
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