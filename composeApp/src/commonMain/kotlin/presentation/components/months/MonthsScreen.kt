package presentation.components.months

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import domain.model.AgendaDay
import kotlinx.datetime.LocalDate

@Composable
fun MonthsScreen(
    allDays: List<AgendaDay>,
    today: LocalDate,
    todayIndex: Int,
    onNowClicked: Boolean
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex - 1, initialFirstVisibleItemScrollOffset = 5
    )

    val months = allDays.groupBy { Pair(it.year, it.month) }

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = months.keys.toList()) { index, item ->
            MonthGrid(
                year = item.first,
                month = item.second,
                days = allDays,
                today = today,
                onDayClicked = {}
            )

        }

//        itemsIndexed(items = months.entries, key = { _, month -> month.id }) { index, month ->
//            MonthGrid(
//                agendaMonth = month,
//                isCurrentMonth = month.id == today.uniqueMonthID(),
//                today = today,
//                onDayClicked = {}
//            )
//        }
    }
}