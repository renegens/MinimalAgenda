package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import domain.model.AgendaMonthYear
import domain.model.AppThemeMode
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.ActionButton
import presentation.components.ComposableLifecycle
import presentation.components.NotificationPermissionDialog
import presentation.screens.add_event.AddEventScreen
import presentation.screens.calendar.day.DayScreen
import presentation.screens.calendar.months.MonthsScreen
import presentation.screens.calendar.years.YearsScreen
import presentation.screens.calendar.CalendarContract.*
import presentation.screens.settings.SettingsScreen

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CalendarScreen(appThemeMode: AppThemeMode) {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        pageCount = { Section.entries.size },
        initialPage = Section.Day.position
    )

    var isScrolledToToday by remember { mutableStateOf(true) }
    var isScrolledToMonth by remember { mutableStateOf(true) }
    var isScrolledToYear by remember { mutableStateOf(true) }

    var dayScrollPosition by remember { mutableStateOf(-1) }
    var yearScrollPosition by remember { mutableStateOf(-1) }
    var monthScrollPosition by remember { mutableStateOf<AgendaMonthYear?>(null) }

    var shouldShowNotificationPermission by remember { mutableStateOf(false) }

    var shouldShowNewEventScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.ScrollDayToToday -> isScrolledToToday = !isScrolledToToday
                Effect.ScrollMonthToToday -> isScrolledToMonth = !isScrolledToMonth
                Effect.ScrollYearToToday -> isScrolledToYear = !isScrolledToYear
                is Effect.GoToDay -> {
                    pagerState.animateScrollToPage(page = Section.Day.position)
                    dayScrollPosition = it.index
                }

                is Effect.GoToMonth -> {
                    pagerState.animateScrollToPage(page = Section.Month.position)
                    monthScrollPosition = it.agendaMonthYear
                }

                is Effect.GoToYear -> {
                    pagerState.animateScrollToPage(page = Section.Year.position)
                    yearScrollPosition = it.agendaYear.year
                }

                is Effect.NotificationPermissionMissing -> {
                    shouldShowNotificationPermission = true
                }
            }
        }
    }

    ComposableLifecycle {
        if (it == Lifecycle.Event.ON_START) {
            viewModel.setEvent(Event.LoadData)
        }
    }

    if (shouldShowNewEventScreen) {
        AddEventScreen(
            onCancel = { shouldShowNewEventScreen = false },
            onSave = {
                viewModel.setEvent(Event.LoadData)
                shouldShowNewEventScreen = false
            })
        return
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        if (shouldShowNotificationPermission) {
            NotificationPermissionDialog(
                onDismissRequest = {
                    shouldShowNotificationPermission = false
                })
        }
        HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
            when (page) {
                CalendarContract.Section.Day.position -> DayScreen(
                    appThemeMode = appThemeMode,
                    days = state.days,
                    currentDate = state.currentDate,
                    onNowClicked = isScrolledToToday,
                    scrollToDay = dayScrollPosition,
                    eventsByDay = state.agendaEvents.groupBy { it.epochDay!! },
                    calendarPermission = state.calendarPermission,
                    showAll = state.showAll,
                    onNotify = { viewModel.setEvent(Event.Notify(it)) },
                    onHide = { viewModel.setEvent(Event.ToggleEventVisibility(it)) },
                    onRequestCalendarPermission = {
                        viewModel.setEvent(Event.OnRequestCalendarPermissionClicked)
                    }
                )

                CalendarContract.Section.Month.position -> MonthsScreen(
                    appThemeMode = appThemeMode,
                    days = state.days,
                    events = state.agendaEvents,
                    currentDate = state.currentDate,
                    onNowClicked = isScrolledToMonth,
                    scrollToMonth = monthScrollPosition,
                    showAll = state.showAll,
                    onDayClicked = {
                        viewModel.setEvent(Event.OnDayClicked(epochDay = it))
                    },
                    onYearClicked = {
                        viewModel.setEvent(Event.OnYearClicked(it))
                    }
                )

                CalendarContract.Section.Year.position -> YearsScreen(
                    appThemeMode = appThemeMode,
                    days = state.days,
                    events = state.agendaEvents,
                    showAll = state.showAll,
                    currentDate = state.currentDate,
                    onMonthClicked = { viewModel.setEvent(Event.OnMonthClicked(it)) },
                    onNowClicked = isScrolledToYear,
                    scrollToYear = yearScrollPosition
                )

                CalendarContract.Section.Settings.position -> SettingsScreen()
            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomEnd).padding(vertical = 64.dp),
        ) {
            ActionButton(
                currentSection = Section.fromInt(pagerState.currentPage),
                calendarPermission = state.calendarPermission,
                showAll = state.showAll,
                nowClicked = {
                    viewModel.setEvent(
                        Event.OnNowClicked(Section.fromInt(pagerState.currentPage))
                    )
                }, toggleActive = {
                    viewModel.setEvent(Event.ToggleActive)
                }, onAdd = {
                    shouldShowNewEventScreen = true
                })
        }
    }
}