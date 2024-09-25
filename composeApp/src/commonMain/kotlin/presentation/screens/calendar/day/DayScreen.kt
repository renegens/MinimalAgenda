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
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import domain.model.AgendaEvent
import domain.model.AppThemeMode
import domain.model.DayListViewType
import extensions.scrollToNowIfNeeded
import kotlinx.datetime.LocalDate
import platform.CalendarPermissionHandler
import presentation.components.CalendarPermission
import presentation.components.DayHeader
import presentation.components.PaperBackground
import presentation.components.SectionDivider
import presentation.components.addHapticFeedback
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium
import presentation.theme.DMSansBlack36

@Composable
fun DayScreen(
    items: List<DayListViewType>,
    appThemeMode: AppThemeMode,
    onNowClicked: Boolean,
    scrollToDay: Int,
    currentDate: LocalDate,
    showAll: Boolean,
    onHide: (AgendaEvent) -> Unit,
    calendarPermission: CalendarPermissionHandler.State,
    onRequestCalendarPermission: () -> Unit,
    onNotify: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit
) {
    if (items.isEmpty()) return

    val currentEpochDay = currentDate.toEpochDays()
    val todayIndex by mutableStateOf(items.findIndexOf(currentEpochDay))

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex)

    LaunchedEffect(todayIndex) {
        listState.scrollToNowIfNeeded(todayIndex)
    }

    LaunchedEffect(onNowClicked) {
        listState.scrollToNowIfNeeded(todayIndex, shouldAnimate = true)
    }

    LaunchedEffect(scrollToDay) {
        if (scrollToDay > 0) {
            val index = items.findIndexOf(scrollToDay)
            listState.scrollToItem(index)
        }
    }

    var selectedItem by remember { mutableStateOf<AgendaEvent?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val eventsByEpochDay: Map<Int, List<DayListViewType.Event>> =
        items.filterIsInstance<DayListViewType.Event>().groupBy { it.event.epochDay!! }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        CalendarPermission(
            onPermissionRequest = onRequestCalendarPermission, state = calendarPermission
        )

        itemsIndexed(
            items = items,
            contentType = { _, item ->
                when (item) {
                    is DayListViewType.Header -> "header"
                    is DayListViewType.Divider -> "divider"
                    is DayListViewType.Event -> "event"
                }
            },
        ) { _, item ->
            PaperBackground(appThemeMode = appThemeMode) {
                when (item) {
                    is DayListViewType.Header -> {
                        Column {
                            Spacer(modifier = Modifier.height(24.dp))
                            DayHeader(
                                day = item.agendaDay.dayInWeek.short,
                                dayInMonth = item.agendaDay.dayOfMonth,
                                month = item.agendaDay.agendaMonthYear.agendaMonth.number,
                                isToday = item.agendaDay.epochDay == currentEpochDay
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            EmptyDay(
                                eventsByEpochDay = eventsByEpochDay,
                                epochDay = item.agendaDay.epochDay,
                                showAll = showAll
                            )
                        }
                    }

                    is DayListViewType.Event -> {
                        Event(
                            event = item.event,
                            onNotify = onNotify,
                            onHide = onHide,
                            onEdit = onEdit,
                            selectedItem = selectedItem,
                            onItemSelected = {
                                selectedItem = it
                            },
                            showAll = showAll,
                            hapticFeedback = hapticFeedback
                        )
                    }

                    is DayListViewType.Divider -> {
                        Column {
                            Spacer(modifier = Modifier.height(32.dp))
                            SectionDivider(isCurrent = item.epochDay == currentEpochDay)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyDay(eventsByEpochDay: Map<Int, List<DayListViewType.Event>>, epochDay: Int, showAll: Boolean) {
    if (eventsByEpochDay[epochDay] == null) {
        Spacer(modifier = Modifier.height(16.dp))
        EmptyMessage()
    } else {

        if (showAll) {
            return
        }

        val hasAllEventsHidden =
            eventsByEpochDay[epochDay]?.none { it.event.isHidden.not() }
        if (hasAllEventsHidden == true) {
            EmptyMessage()
        }
    }
}

@Composable
fun Event(
    event: AgendaEvent,
    onNotify: (AgendaEvent) -> Unit,
    onHide: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    selectedItem: AgendaEvent?,
    onItemSelected: (AgendaEvent?) -> Unit,
    showAll: Boolean,
    hapticFeedback: HapticFeedback
) {
    if (showAll.not() && event.isHidden) return

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Time(agendaEvent = event, showAll = showAll)
        Column(
            modifier = Modifier.fillMaxWidth().background(
                color = if (selectedItem == event) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
            ).clickable {
                hapticFeedback.addHapticFeedback()
                if (selectedItem == event) {
                    onItemSelected.invoke(null)
                } else {
                    onItemSelected.invoke(event)
                }
            }
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = event.title,
                style = DMSans18Medium,
                color = event.getTextColor(false),
            )
            AnimatedVisibility(visible = selectedItem == event) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(onClick = {
                        onHide.invoke(event)
                        onItemSelected.invoke(null)
                    }) {
                        val text = if (event.isHidden) {
                            "Show"
                        } else {
                            "Hide"
                        }
                        Text(text.uppercase(), style = DMSansBlack36)
                    }
                    TextButton(onClick = {
                        onNotify.invoke(event)
                        onItemSelected.invoke(null)
                    }) {
                        Text("Notify".uppercase(), style = DMSansBlack36)
                    }
                    TextButton(onClick = {
                        onEdit.invoke(event)
                        onItemSelected.invoke(null)
                    }) {
                        Text("Edit".uppercase(), style = DMSansBlack36)
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
fun EmptyMessage() {
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

private fun List<DayListViewType>.findIndexOf(epochDay: Int): Int {
    return indexOfFirst {
        (it is DayListViewType.Header) && it.agendaDay.epochDay == epochDay
    }
}
