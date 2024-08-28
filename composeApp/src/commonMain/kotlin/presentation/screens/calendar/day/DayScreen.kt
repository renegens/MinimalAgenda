package presentation.screens.calendar.day

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.AgendaDay
import domain.model.AgendaEvent
import kotlinx.datetime.LocalDate
import platform.CalendarPermissionHandler
import presentation.components.CalendarPermission
import presentation.components.DayHeader
import presentation.components.PaperBackground
import presentation.components.SectionDivider
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium
import presentation.theme.DMSansBlack36

@Composable
fun DayScreen(
    days: List<AgendaDay>,
    eventsByDay: Map<Int, List<AgendaEvent>>,
    onNowClicked: Boolean,
    scrollToDay: Int,
    currentDate: LocalDate,
    showAll: Boolean,
    onHide: (AgendaEvent) -> Unit,
    calendarPermission: CalendarPermissionHandler.State,
    onRequestCalendarPermission: () -> Unit,
    onNotify: (AgendaEvent) -> Unit
) {
    if (days.isEmpty()) return
    val currentEpochDay by remember { mutableStateOf(currentDate.toEpochDays()) }

    val todayIndex by remember { mutableStateOf(days.indexOfFirst { it.epochDay == currentEpochDay}) }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex + 1, initialFirstVisibleItemScrollOffset = 5
    )

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LaunchedEffect(scrollToDay) {
        if (scrollToDay > 0) {
            listState.animateScrollToItem(scrollToDay)
        }
    }

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {

        CalendarPermission(
            onPermissionRequest = onRequestCalendarPermission, state = calendarPermission
        )
        itemsIndexed(items = days, key = { index, day -> day.epochDay }) { index, day ->
            val isToday = day.epochDay == currentEpochDay
            PaperBackground {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    DayHeader(
                        day = day.dayInWeek.short,
                        dayInMonth = day.dayOfMonth,
                        month = day.agendaMonthYear.agendaMonth.number,
                        isToday = isToday
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Entries(
                        agendaEvents = eventsByDay[day.epochDay] ?: emptyList(),
                        onHide = onHide,
                        onNotify = onNotify,
                        showAll = showAll
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionDivider(isCurrent = isToday)
                }
            }
        }
    }
}

@Composable
fun Entries(
    agendaEvents: List<AgendaEvent>,
    onNotify: (AgendaEvent) -> Unit,
    onHide: (AgendaEvent) -> Unit,
    showAll: Boolean
) {

    val items = if (showAll) {
        agendaEvents
    } else {
        agendaEvents.filterNot { it.isHidden }
    }

    if (items.isEmpty()) {
        EmptyDay()
        return
    }

    items.sortedBy { it.startTime }.forEach {
        var showButtons by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
        ) {
            Time(agendaEvent = it, showAll = showAll)
            Column(
                modifier = Modifier.fillMaxWidth().background(
                    color = if (showButtons) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                ).clickable {
                    showButtons = !showButtons
                }
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = it.title,
                    style = DMSans18Medium,
                    color = it.getTextColor(showAll),
                )
                AnimatedVisibility(visible = showButtons) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(onClick = {
                            onHide.invoke(it)
                            showButtons = false
                        }) {
                            val text = if (it.isHidden) {
                                "Show"
                            } else {
                                "Hide"
                            }
                            Text(text.uppercase(), style = DMSansBlack36)
                        }
                        TextButton(onClick = {
                            onNotify.invoke(it)
                            showButtons = false
                        }) {
                            Text("Notify".uppercase(), style = DMSansBlack36)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AgendaEvent.getTextColor(showAll: Boolean): Color {
    return when {
        isHidden.not() -> MaterialTheme.colorScheme.tertiary
        isHidden && showAll -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun EmptyDay() {
    Box(
        modifier = Modifier.height(56.dp).fillMaxWidth().padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            "Day is free, enjoy it",
            style = DMSans18Medium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun Time(agendaEvent: AgendaEvent, showAll: Boolean) {
    Row(modifier = Modifier.width(82.dp)) {
        val time = agendaEvent.formattedTime.split(" ")

        if (agendaEvent.allDay) {
            Text(
                text = "All Day",
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
        } else {
            Text(
                modifier = Modifier.width(30.dp),
                text = time[0],
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
            Text(
                ":${time[1]}",
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
        }
    }
}