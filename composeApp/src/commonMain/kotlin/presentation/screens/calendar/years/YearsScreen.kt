package presentation.screens.calendar.years

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaDay
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.today
import kotlinx.datetime.LocalDate
import presentation.components.SectionDivider

@Composable
fun YearsScreen(
    days: List<AgendaDay>,
    onNowClicked: Boolean,
    scrollToYear: Int,
    onMonthClicked: (AgendaMonthYear) -> Unit
) {

    if (days.isEmpty()) return

    val years by remember {
        mutableStateOf(days.groupBy { it.agendaMonthYear.agendaYear })
    }

    val todayIndex by remember {
        mutableStateOf(
           years.keys.indexOfFirst { it.isCurrent }
        )
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex + 1, initialFirstVisibleItemScrollOffset = 2
    )

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LaunchedEffect(scrollToYear) {
        if (scrollToYear > 0){
            listState.scrollToItem(scrollToYear)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(items = years.keys.toList(), key = { _, year -> year.year }) { index, year ->
            YearHeader(year)
            Months(year = year.year, onMonthClicked = onMonthClicked)
            Spacer(modifier = Modifier.height(16.dp))
            SectionDivider(isCurrent = year.year == today.year)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}