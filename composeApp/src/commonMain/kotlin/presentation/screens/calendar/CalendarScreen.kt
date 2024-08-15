package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaMonthYear
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.ActionButton
import presentation.screens.calendar.day.DayScreen
import presentation.screens.calendar.months.MonthsScreen
import presentation.screens.calendar.years.YearsScreen
import presentation.screens.calendar.CalendarContract.*

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CalendarScreen() {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)

    var isScrolledToToday by remember { mutableStateOf(true) }
    var isScrolledToMonth by remember { mutableStateOf(true) }
    var isScrolledToYear by remember { mutableStateOf(true) }

    var dayScrollPosition by remember { mutableStateOf(-1) }
    var yearScrollPosition by remember { mutableStateOf(-1) }
    var monthScrollPosition by remember { mutableStateOf<AgendaMonthYear?>(null) }

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
            }
        }
    }

    Scaffold {
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            HorizontalPager(state = pagerState, beyondViewportPageCount = 1) { page ->
                when (page) {
                    CalendarContract.Section.Day.position -> DayScreen(
                        days = state.days,
                        onNowClicked = isScrolledToToday,
                        scrollToDay = dayScrollPosition,
                        agendaEvents = state.agendaEvents,
                        calendarPermission = state.calendarPermission,
                        showAll = state.showAll,
                        onHide = { viewModel.setEvent(Event.ToggleEventVisibility(it)) },
                        onRequestCalendarPermission = {
                            viewModel.setEvent(Event.OnRequestCalendarPermissionClicked)
                        }
                    )

                    CalendarContract.Section.Month.position -> MonthsScreen(
                        days = state.days,
                        onNowClicked = isScrolledToMonth,
                        scrollToMonth = monthScrollPosition,
                        onDayClicked = {
                            viewModel.setEvent(Event.OnDayClicked(epochDay = it))
                        },
                        onYearClicked = {
                            viewModel.setEvent(Event.OnYearClicked(it))
                        }
                    )

                    CalendarContract.Section.Year.position -> YearsScreen(
                        days = state.days,
                        onMonthClicked = { viewModel.setEvent(Event.OnMonthClicked(it)) },
                        onNowClicked = isScrolledToYear,
                        scrollToYear = yearScrollPosition
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(vertical = 64.dp),
            ) {
                ActionButton(
                    showAll = state.showAll,
                    nowClicked = {
                    viewModel.setEvent(
                        Event.OnNowClicked(Section.fromInt(pagerState.currentPage))
                    )
                }, toggleActive = {
                    viewModel.setEvent(Event.ToggleActive)
                })
            }
        }
    }
}