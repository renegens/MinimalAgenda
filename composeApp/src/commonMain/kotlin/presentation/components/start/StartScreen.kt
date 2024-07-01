package presentation.components.start

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.DateGenerator
import data.byYears
import data.toDayData
import data.toMonthData
import extensions.uniqueMonthID
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import presentation.components.day.DayScreen
import presentation.components.months.MonthsScreen
import presentation.components.years.YearsScreen
import presentation.theme.CustomColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartScreen(modifier: Modifier = Modifier) {

    val data = DateGenerator().generateDays()

    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val pagerState = rememberPagerState(pageCount = {
        3
    }, initialPage = 0)

    val monthData = data.toMonthData()
    val dayData = data.toDayData()
    val monthIndexToScrollTo = monthData.indexOfFirst { it.id == currentDate.uniqueMonthID() }
    val dayIndexToScrollTo = dayData.last().epochDay - currentDate.toEpochDays()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, beyondBoundsPageCount = 3) { page ->
            when (page) {
                0 -> DayScreen(days = dayData, today = currentDate, todayIndex = dayIndexToScrollTo)
                1 -> MonthsScreen(
                    months = monthData,
                    today = currentDate,
                    todayIndex = monthIndexToScrollTo
                )

                2 -> YearsScreen(data.byYears())
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
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
        Spacer(modifier = Modifier.height(32.dp))
    }


}