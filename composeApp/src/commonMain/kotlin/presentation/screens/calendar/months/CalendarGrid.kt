package presentation.screens.calendar.months

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import domain.model.DayGrid
import domain.model.createGridDay
import extensions.hasEvents
import extensions.today
import presentation.components.MonthHeader
import presentation.components.SectionDivider
import presentation.theme.DMSans18SemiBold

@Composable
fun MonthGrid(
    monthYear: AgendaMonthYear,
    events: List<AgendaEvent>,
    onDayClicked: (Int) -> Unit,
    onYearClicked: (AgendaYear) -> Unit,
    showAll: Boolean
) {
    val isCurrent by remember { mutableStateOf(monthYear.isCurrent) }

    Spacer(modifier = Modifier.height(24.dp))
    MonthHeader(monthYear = monthYear,
        modifier = Modifier.clickable { onYearClicked.invoke(monthYear.agendaYear) })
    Spacer(modifier = Modifier.height(16.dp))
    DaysInWeek(today = today, isCurrentMonth = isCurrent)
    Spacer(modifier = Modifier.height(24.dp))
    CalendarDays(
        events = events, monthYear = monthYear, onDayClicked = onDayClicked, showAll = showAll
    )
    Spacer(modifier = Modifier.height(32.dp))
    SectionDivider(isCurrent = isCurrent)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(
    events: List<AgendaEvent>,
    showAll: Boolean,
    monthYear: AgendaMonthYear,
    onDayClicked: (Int) -> Unit
) {
    val gridDays by derivedStateOf {
        createGridDay(monthYear)
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 7,
    ) {
        gridDays.forEach { gridItem ->

            val isToday by derivedStateOf {
                gridItem.epochDay == today.toEpochDays()
            }

            if (gridItem.dayInMonth == DayGrid.DONT_RENDER) {
                Box(modifier = Modifier.height(40.dp).width(48.dp))
            } else {
                val hasEvent =
                    events.hasEvents(epochDay = gridItem.epochDay, showAll = showAll)

                Box(
                    modifier = Modifier.height(40.dp).width(48.dp)
                        .clickable { onDayClicked.invoke(gridItem.epochDay) }
                ) {
                    Text(
                        text = gridItem.dayInMonth.toString(),
                        style = DMSans18SemiBold,
                        textAlign = TextAlign.Start,
                        color = getTextColor(hasEvent, isToday = isToday),
                        modifier = Modifier.wrapContentHeight(unbounded = true)
                    )
                }
            }
        }
    }
}

@Composable
private fun getTextColor(hasEvent: Boolean, isToday: Boolean): Color {
    return when {
        isToday -> MaterialTheme.colorScheme.primary
        hasEvent -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
}