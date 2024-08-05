package presentation.components.months

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import data.MonthData
import extensions.uniqueMonthID
import kotlinx.datetime.LocalDate

@Composable
fun MonthsScreen(months: List<MonthData>, today: LocalDate, todayIndex: Int) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex - 1, initialFirstVisibleItemScrollOffset = 5)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = months,
            key = { _, month -> month.id }
        ) { index, month ->
            MonthGrid(
                monthData = month,
                isCurrentMonth = month.id == today.uniqueMonthID(),
                today = today
            )
        }
    }
}