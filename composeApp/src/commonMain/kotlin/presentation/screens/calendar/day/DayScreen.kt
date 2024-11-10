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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import domain.model.AgendaEvent
import domain.model.AppThemeMode
import domain.model.DayListViewType
import extensions.scrollToNowIfNeeded
import extensions.today
import kotlinx.datetime.LocalDate
import platform.CalendarPermissionHandler
import presentation.components.CalendarPermission
import presentation.components.DayHeader
import presentation.components.ScrollablePaperBackground
import presentation.components.SectionDivider
import presentation.components.addHapticFeedback
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium
import presentation.theme.DMSansBlack36

private val STICKY_HEADER_INDEX = 1

@Composable
fun DayScreen(
    items: List<DayListViewType>,
    appThemeMode: AppThemeMode,
    onNowClicked: Boolean,
    scrollToDay: Int,
    currentDate: LocalDate,
    showAll: Boolean,
    scrollToNow: Boolean,
    onHide: (AgendaEvent) -> Unit,
    calendarPermission: CalendarPermissionHandler.State,
    onRequestCalendarPermission: () -> Unit,
    onNotify: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    isPresentingNotificationOnBoarding: Boolean
) {
    if (items.isEmpty()) return

    val currentEpochDay = currentDate.toEpochDays()

    val todayIndex by if (calendarPermission == CalendarPermissionHandler.State.FullAccess) {
        mutableStateOf(items.findIndexOf(currentEpochDay))
    } else {
        mutableStateOf(items.findIndexOf(currentEpochDay) - STICKY_HEADER_INDEX)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(onNowClicked) {
        listState.scrollToNowIfNeeded(todayIndex, shouldAnimate = true)
    }

    LaunchedEffect(scrollToDay) {
        if (scrollToDay > 0) {
            val index = items.findIndexOf(scrollToDay)
            listState.scrollToItem(index)
        }
    }

    SideEffect {
        if (scrollToNow) {
            listState.requestScrollToItem(index = todayIndex)
        }
    }

    var selectedItem by remember { mutableStateOf<AgendaEvent?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val eventsByEpochDay: Map<Int, List<DayListViewType.Event>> =
        items.filterIsInstance<DayListViewType.Event>().groupBy { it.event.epochDay!! }

    ScrollablePaperBackground(appThemeMode, listState) {

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {

            if (isPresentingNotificationOnBoarding.not()){
                CalendarPermission(
                    onPermissionRequest = onRequestCalendarPermission, state = calendarPermission
                )
            }

            items(items = items, key = { item -> item.uniqueId }, contentType = { item ->
                when (item) {
                    is DayListViewType.Header -> "header"
                    is DayListViewType.Divider -> "divider"
                    is DayListViewType.Event -> "event"
                }
            }) { item ->
                when (item) {
                    is DayListViewType.Header -> {
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
fun EmptyDay(
    eventsByEpochDay: Map<Int, List<DayListViewType.Event>>,
    epochDay: Int,
    showAll: Boolean
) {
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
            val style = if (event.allDay.not() && event.hasPassedForToday(today.toEpochDays())) {
                DMSans18Medium.copy(textDecoration = TextDecoration.LineThrough)
            } else {
                DMSans18Medium
            }

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = event.title,
                style = style,
                color = event.getTextColor(showAll),
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
    val time = agendaEvent.getFormattedTime()
    Row(modifier = Modifier.width(time.getRowWidth().dp).padding(end = 8.dp)) {
        if (agendaEvent.allDay) {
            Text(
                text = "All Day",
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
        } else {
            Text(
                modifier = Modifier.weight(0.6F),
                text = time.hours,
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
            Text(
                modifier = Modifier.weight(1F),
                text = time.minutes,
                style = DMSans18Bold60,
                color = agendaEvent.getTextColor(showAll)
            )
            time.ampm?.let {
                Text(
                    modifier = Modifier.weight(1F),
                    text = it,
                    style = DMSans18Bold60,
                    color = agendaEvent.getTextColor(showAll)
                )
            }
        }
    }
}

private fun List<DayListViewType>.findIndexOf(epochDay: Int): Int {
    return indexOfFirst {
        (it is DayListViewType.Header) && it.agendaDay.epochDay == epochDay
    }
}
