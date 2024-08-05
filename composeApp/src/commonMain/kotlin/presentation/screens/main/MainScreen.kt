package presentation.screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import presentation.components.day.DayScreen
import presentation.components.months.MonthsScreen
import presentation.components.years.YearsScreen
import presentation.theme.CustomColor

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainScreen() {

    val viewModel: MainViewModel = koinViewModel()

    val state by viewModel.uiState.collectAsState()

    val pagerState = rememberPagerState(pageCount = {
        3
    }, initialPage = 0)

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
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(vertical = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = {}, modifier = Modifier.background(CustomColor.Green)) {
                    Text("Day", color = Color.White)
                }
                TextButton(onClick = {}, modifier = Modifier.background(CustomColor.Green)) {
                    Text("Day", color = Color.White)
                }
                TextButton(onClick = {}, modifier = Modifier.background(CustomColor.Green)) {
                    Text("Day", color = Color.White)
                }
            }
        }
    }
}
