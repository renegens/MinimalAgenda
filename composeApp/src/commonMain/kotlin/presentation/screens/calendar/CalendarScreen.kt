package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.day.DayScreen
import presentation.components.months.MonthsScreen
import presentation.components.years.YearsScreen

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CalendarScreen() {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)

    Scaffold(modifier = Modifier.safeDrawingPadding(), floatingActionButton = {
        FloatingActionButton(
            onClick = {},
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Text("Today")
        }
    }) {
        state.calendar?.let { calendar ->
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
                    when (page) {
                        0 -> DayScreen(
                            days = calendar.days,
                            today = calendar.today,
                            todayIndex = calendar.dayIndex
                        )

                        1 -> MonthsScreen(
                            months = calendar.months,
                            today = calendar.today,
                            todayIndex = calendar.monthIndex
                        )

                        2 -> YearsScreen(
                            years = calendar.years,
                            today = calendar.today,
                            todayIndex = calendar.yearIndex
                        )
                    }
                }
            }
        }
    }
}
