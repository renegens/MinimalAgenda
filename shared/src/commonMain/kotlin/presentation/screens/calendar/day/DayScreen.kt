package presentation.screens.calendar.day

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
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
import presentation.components.DayHeader
import presentation.components.EventDetailsBottomSheet
import presentation.components.EventOptionsMenu
import presentation.components.ScrollablePaperBackground
import presentation.components.SectionDivider
import presentation.components.addHapticFeedbackProminent
import presentation.components.noRippleClickable
import presentation.theme.DMSans18Bold60
import presentation.theme.DMSans18Medium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    items: List<DayListViewType>,
    appThemeMode: AppThemeMode,
    onNowClicked: Int,
    scrollToDay: Long,
    currentDate: LocalDate,
    showAll: Boolean,
    scrollToNow: Boolean,
    onHide: (AgendaEvent) -> Unit,
    onNotify: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    onAddEvent: (LocalDate) -> Unit,
    onDetails: (AgendaEvent) -> Unit,
    onOpenSettings: () -> Unit,
    isDemoMode: Boolean,
) {
    if (items.isEmpty()) return

    val currentEpochDay = currentDate.toEpochDays()

    // Recalculate todayIndex when items change (important for initial load when items load asynchronously)
    val todayIndex = remember(items, currentEpochDay) { 
        items.findIndexOf(currentEpochDay) 
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

    // Watch both scrollToNow and todayIndex to handle:
    // 1. scrollToNow changes to true when todayIndex is already valid
    // 2. todayIndex becomes valid after scrollToNow is already true (async data load)
    LaunchedEffect(scrollToNow, todayIndex) {
        if (scrollToNow && todayIndex >= 0) {
            listState.scrollToItem(index = todayIndex)
        }
    }

    var selectedItem by remember { mutableStateOf<AgendaEvent?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val eventsByEpochDay: Map<Long, List<DayListViewType.Event>> =
        items.filterIsInstance<DayListViewType.Event>().groupBy { it.event.epochDay!! }
    
    // Bottom sheet state for event details
    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDetailsBottomSheet by remember { mutableStateOf(false) }
    var selectedEventForDetails by remember { mutableStateOf<AgendaEvent?>(null) }
    val scope = rememberCoroutineScope()

    ScrollablePaperBackground(appThemeMode, listState) {

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
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
                        val date = LocalDate.fromEpochDays(item.agendaDay.epochDay)
                        Box(Modifier.fillMaxWidth().clickable(onClickLabel = "Create event") {
                            onAddEvent(date)
                        }) {
                            DayHeader(
                                day = item.agendaDay.dayInWeek.short,
                                dayInMonth = item.agendaDay.dayOfMonth,
                                month = item.agendaDay.agendaMonthYear.agendaMonth.number,
                                isToday = item.agendaDay.epochDay == currentEpochDay
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        EmptyDay(
                            eventsByEpochDay = eventsByEpochDay,
                            epochDay = item.agendaDay.epochDay,
                            showAll = showAll,
                            onAddEvent = { onAddEvent(date) }
                        )
                    }

                    is DayListViewType.Event -> {
                        Event(
                            event = item.event,
                            onNotify = onNotify,
                            onHide = onHide,
                            onEdit = onEdit,
                            onDetails = {
                                selectedEventForDetails = it
                                showDetailsBottomSheet = true
                            },
                            onOpenSettings = onOpenSettings,
                            selectedItem = selectedItem,
                            onItemSelected = {
                                selectedItem = it
                            },
                            showAll = showAll,
                            hapticFeedback = hapticFeedback,
                            isDemoMode = isDemoMode
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
    
    // Event details bottom sheet
    if (showDetailsBottomSheet) {
        selectedEventForDetails?.let { event ->
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = {
                    scope.launch { detailsSheetState.hide() }.invokeOnCompletion {
                        if (!detailsSheetState.isVisible) {
                            showDetailsBottomSheet = false
                        }
                    }
                },
                sheetState = detailsSheetState
            ) {
                EventDetailsBottomSheet(
                    event = event,
                    onDismiss = {
                        scope.launch { detailsSheetState.hide() }.invokeOnCompletion {
                            if (!detailsSheetState.isVisible) {
                                showDetailsBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyDay(
    eventsByEpochDay: Map<Long, List<DayListViewType.Event>>,
    epochDay: Long,
    showAll: Boolean,
    onAddEvent: () -> Unit,
) {
    if (eventsByEpochDay[epochDay] == null) {
        Spacer(modifier = Modifier.height(16.dp))
        EmptyMessage(onAddEvent)
    } else {

        if (showAll) {
            return
        }

        val hasAllEventsHidden =
            eventsByEpochDay[epochDay]?.none { it.event.isHidden.not() }
        if (hasAllEventsHidden == true) {
            EmptyMessage(onAddEvent)
        }
    }
}

@Composable
fun Event(
    event: AgendaEvent,
    onNotify: (AgendaEvent) -> Unit,
    onHide: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    onDetails: (AgendaEvent) -> Unit,
    onOpenSettings: () -> Unit,
    selectedItem: AgendaEvent?,
    onItemSelected: (AgendaEvent?) -> Unit,
    showAll: Boolean,
    hapticFeedback: HapticFeedback,
    isDemoMode: Boolean
) {
    if (showAll.not() && event.isHidden) return
    val textColor = if (selectedItem == event) {
        MaterialTheme.colorScheme.secondary
    } else {
        event.getTextColor(showAll)
    }
    Row(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 28.dp).wrapContentHeight()
            .padding(horizontal = 16.dp).noRippleClickable {
                hapticFeedback.addHapticFeedbackProminent()
                // Special handling for demo event with id "6" - open settings instead of menu
                if (event.id == "6" && isDemoMode) {
                    onOpenSettings()
                } else {
                    if (selectedItem == event) {
                        onItemSelected.invoke(null)
                    } else {
                        onItemSelected.invoke(event)
                    }
                }
            }
    ) {
        Time(agendaEvent = event, textColor = textColor)
        Column(modifier = Modifier.fillMaxWidth()) {

            val style = when {
                isDemoMode -> DMSans18Medium
                event.allDay.not() && event.hasPassedForToday(today.toEpochDays()) -> DMSans18Medium.copy(textDecoration = TextDecoration.LineThrough)
                else -> DMSans18Medium
            }

            val title = if (event.isLastDayInMultiDayEvent) {
                "${event.title} (Ending)"
            } else {
                event.title
            }
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = title,
                style = style,
                color = textColor,
            )
        }

        EventOptionsMenu(
            event = event,
            onNotify = onNotify,
            onHide = onHide,
            onEdit = onEdit,
            onDetails = onDetails,
            selectedItem = selectedItem,
            hapticFeedback = hapticFeedback,
            onItemSelected = onItemSelected,
            isDemoMode = isDemoMode
        )
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
fun EmptyMessage(onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.height(56.dp).fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClickLabel = "Create event", onClick = onClick) else Modifier)
            .padding(horizontal = 24.dp),
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
fun Time(agendaEvent: AgendaEvent, textColor: Color) {
    val time = agendaEvent.getFormattedTime()

    Row(modifier = Modifier.width(time.getRowWidth().dp).padding(end = 8.dp)) {
        if (agendaEvent.displaysAllDay) {
            Text(
                text = "All Day",
                style = DMSans18Bold60,
                color = textColor
            )
        } else {
            Text(
                modifier = Modifier.weight(0.6F),
                text = time.hours,
                style = DMSans18Bold60,
                color = textColor
            )
            Text(
                modifier = Modifier.weight(1F),
                text = time.minutes,
                style = DMSans18Bold60,
                color = textColor
            )
            time.ampm?.let {
                Text(
                    modifier = Modifier.weight(1F),
                    text = it,
                    style = DMSans18Bold60,
                    color = textColor
                )
            }
        }
    }
}

private fun List<DayListViewType>.findIndexOf(epochDay: Long): Int {
    return indexOfFirst {
        (it is DayListViewType.Header) && it.agendaDay.epochDay == epochDay
    }
}
