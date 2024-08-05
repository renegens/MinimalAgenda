package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.ActionButton
import presentation.components.day.DayScreen
import presentation.components.months.MonthsScreen
import presentation.components.years.YearsScreen

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CalendarScreen() {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)

    var isScrolledToToday by remember { mutableStateOf(true) }
    var isScrolledToMonth by remember { mutableStateOf(true) }
    var isScrolledToYear by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                CalendarContract.Effect.ScrollDayToToday -> isScrolledToToday = !isScrolledToToday
                CalendarContract.Effect.ScrollMonthToToday -> isScrolledToMonth = !isScrolledToMonth
                CalendarContract.Effect.ScrollYearToToday -> isScrolledToYear = !isScrolledToYear
            }
        }
    }


    Scaffold(modifier = Modifier.safeDrawingPadding()) {
        state.calendar?.let { calendar ->
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
                    when (page) {
                        CalendarContract.Section.Day.position -> DayScreen(
                            days = calendar.days,
                            today = calendar.today,
                            todayIndex = calendar.dayIndex,
                            onNowClicked = isScrolledToToday,
                        )

                        CalendarContract.Section.Month.position -> MonthsScreen(
                            months = calendar.months,
                            today = calendar.today,
                            todayIndex = calendar.monthIndex
                        )

                        CalendarContract.Section.Year.position -> YearsScreen(
                            years = calendar.years,
                            today = calendar.today,
                            todayIndex = calendar.yearIndex
                        )
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(vertical = 64.dp),
                ) {
                    ActionButton(nowClicked = {
                        viewModel.setEvent(
                            CalendarContract.Event.OnNowClicked(
                                CalendarContract.Section.fromInt(
                                    pagerState.currentPage
                                )
                            )
                        )
                    }, toggleActive = {})
                }
            }
        }
    }
}
