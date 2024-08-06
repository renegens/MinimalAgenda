package presentation.components.years

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.AgendaYear
import kotlinx.datetime.LocalDate
import presentation.components.SectionDivider

@Composable
fun YearsScreen(years: List<AgendaYear>, today: LocalDate, todayIndex: Int, onNowClicked: Boolean) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex + 1,
        initialFirstVisibleItemScrollOffset = 5
    )

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    ) {
        itemsIndexed(
            items = years,
            key = { _, year -> year.year }
        ) { index, year ->
            YearHeader(year = year, isCurrentYear = today.year == year.year)
            Months(renderingYear = year.year, today = today)
            Spacer(modifier = Modifier.height(16.dp))
            SectionDivider(isCurrent = year.year == today.year)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}