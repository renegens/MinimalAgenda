package presentation.screens.calendar

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.Lifecycle
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AppThemeMode
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.ActionButton
import presentation.components.ComposableLifecycle
import presentation.components.NotificationPermissionDialog
import presentation.components.addHapticFeedbackProminent
import presentation.screens.calendar.CalendarContract.Effect
import presentation.screens.calendar.CalendarContract.Event
import presentation.screens.calendar.CalendarContract.Section
import presentation.screens.calendar.day.DayScreen
import presentation.screens.calendar.months.MonthsScreen
import presentation.screens.calendar.years.YearsScreen
import presentation.screens.settings.SettingsScreen

@Composable
fun CalendarScreen(
    appThemeMode: AppThemeMode,
    onAddEvent: () -> Unit,
    onEditEvent: (AgendaEvent) -> Unit,
    notEligible: () -> Unit,
    forceEventLoad: Int
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        pageCount = { Section.entries.size },
        initialPage = Section.Day.position,
    )
    val haptic = LocalHapticFeedback.current

    var isScrolledToToday by remember { mutableStateOf(true) }
    var isScrolledToMonth by remember { mutableStateOf(true) }
    var isScrolledToYear by remember { mutableStateOf(true) }

    var dayScrollPosition by remember { mutableStateOf(-1) }
    var yearScrollPosition by remember { mutableStateOf(-1) }
    var monthScrollPosition by remember { mutableStateOf<AgendaMonthYear?>(null) }

    var shouldShowNotificationPermission by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            haptic.addHapticFeedbackProminent()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.ScrollDayToToday -> isScrolledToToday = !isScrolledToToday
                Effect.ScrollMonthToToday -> isScrolledToMonth = !isScrolledToMonth
                Effect.ScrollYearToToday -> isScrolledToYear = !isScrolledToYear
                is Effect.GoToDay -> {
                    pagerState.animateScrollToPage(page = Section.Day.position)
                    dayScrollPosition = it.epochDay
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

                is Effect.ShowPaywall -> notEligible.invoke()
                is Effect.ShowEdit -> onEditEvent.invoke(it.agendaEvent)
            }
        }
    }

    ComposableLifecycle {
        if (it == Lifecycle.Event.ON_START) {
            viewModel.setEvent(Event.LoadData)
        }
    }

    LaunchedEffect(forceEventLoad) {
        if (forceEventLoad > 0) {
            viewModel.setEvent(Event.LoadData)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.statusBarsPadding(),
        floatingActionButton = {
            ActionButton(
                currentSection = Section.fromInt(pagerState.currentPage),
                calendarPermission = state.calendarPermission,
                showAll = state.showAll,
                nowClicked = {
                    viewModel.setEvent(
                        Event.OnNowClicked(Section.fromInt(pagerState.currentPage))
                    )
                },
                toggleActive = {
                    viewModel.setEvent(Event.ToggleActive)
                },
                onAdd = onAddEvent
            )
        }) {
        if (shouldShowNotificationPermission) {
            NotificationPermissionDialog(onDismissRequest = {
                shouldShowNotificationPermission = false
            })
        }
        HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
            when (page) {
                CalendarContract.Section.Day.position -> DayScreen(appThemeMode = appThemeMode,
                    items = state.flatDaysAndEvents,
                    currentDate = state.currentDate,
                    onNowClicked = isScrolledToToday,
                    scrollToDay = dayScrollPosition,
                    calendarPermission = state.calendarPermission,
                    showAll = state.showAll,
                    onNotify = { viewModel.setEvent(Event.Notify(it)) },
                    onHide = { viewModel.setEvent(Event.ToggleEventVisibility(it)) },
                    onEdit = { viewModel.setEvent(Event.OnEditEvent(it)) },
                    onRequestCalendarPermission = {
                        viewModel.setEvent(Event.OnRequestCalendarPermissionClicked)
                    })

                CalendarContract.Section.Month.position -> MonthsScreen(appThemeMode = appThemeMode,
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
                    })

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

                CalendarContract.Section.Settings.position -> SettingsScreen(notEligible = notEligible)
            }
        }
    }
}