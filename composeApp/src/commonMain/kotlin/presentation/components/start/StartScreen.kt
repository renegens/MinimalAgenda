package presentation.components.start

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    HorizontalPager(state = pagerState) { page ->
        when (page){
            0 -> DayScreen(days = dayData, today = currentDate, todayIndex = dayIndexToScrollTo)
            1 -> MonthsScreen(months = monthData, today = currentDate, todayIndex = monthIndexToScrollTo)
            2 -> YearsScreen(data.byYears())
        }
    }
}