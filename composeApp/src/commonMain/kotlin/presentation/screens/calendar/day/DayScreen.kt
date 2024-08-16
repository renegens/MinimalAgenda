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
import extensions.today
import extensions.todayEpochDay
import platform.CalendarPermissionHandler
import presentation.components.CalendarPermission
import presentation.components.DayHeader
import presentation.components.SectionDivider

@Composable
fun DayScreen(
    days: List<AgendaDay>,
    agendaEvents: List<AgendaEvent>,
    onNowClicked: Boolean,
    scrollToDay: Int,
    showAll: Boolean,
    onHide: (AgendaEvent) -> Unit,
    calendarPermission: CalendarPermissionHandler.State,
    onRequestCalendarPermission: () -> Unit
) {
    if (days.isEmpty()) return

    val todayIndex by remember { mutableStateOf(days.indexOfFirst { it.epochDay == todayEpochDay }) }

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
            val isToday = day.epochDay == today.toEpochDays()
            Spacer(modifier = Modifier.height(32.dp))
            DayHeader(
                day = day.dayInWeek.short,
                dayInMonth = day.dayOfMonth,
                month = day.agendaMonthYear.agendaMonth.number,
                isToday = isToday
            )
            Spacer(modifier = Modifier.height(8.dp))
            Entries(
                agendaEvents = agendaEvents.filter { it.epochDay == day.epochDay },
                onHide = onHide,
                showAll = showAll
            )
            Spacer(modifier = Modifier.height(32.dp))
            SectionDivider(isCurrent = isToday)
        }
    }
}

@Composable
fun Entries(
    agendaEvents: List<AgendaEvent>,
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
                .padding(bottom = 4.dp)
        ) {
            Row {
                val time = it.formatedStartTime
                Text(
                    if (it.allDay) "All Day" else time.first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = it.getTextColor(showAll)
                )
                if (it.allDay.not()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (it.allDay) "" else ":${time.second}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = it.getTextColor(showAll)
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            Column(
                modifier = Modifier.fillMaxWidth().background(
                    color = if (showButtons) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                ).clickable {
                    showButtons = !showButtons
                }
            ) {
                Text(
                    it.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = it.getTextColor(showAll),
                    modifier = Modifier.padding(start = 12.dp)
                )
                AnimatedVisibility(visible = showButtons) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                Text(text)
                            }
                        }
                }
            }

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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}