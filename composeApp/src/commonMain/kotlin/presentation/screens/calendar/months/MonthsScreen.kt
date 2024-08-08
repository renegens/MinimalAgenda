package presentation.screens.calendar.months

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import domain.model.AgendaDay
import domain.model.AgendaMonthYear
import domain.model.AgendaYear
import extensions.today
import extensions.todayEpochDay
import kotlinx.datetime.LocalDate

@Composable
fun MonthsScreen(
    days: List<AgendaDay>,
    onNowClicked: Boolean,
    scrollToMonth: AgendaMonthYear?,
    onDayClicked: (Int) -> Unit,
    onYearClicked: (AgendaYear) -> Unit
) {

    if (days.isEmpty()) return

    val months by remember {
        mutableStateOf(days.groupBy { it.agendaMonthYear })
    }

    val todayIndex by remember {
        mutableStateOf(
            months.keys.indexOfFirst { it.isCurrent }
        )
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex - 1, initialFirstVisibleItemScrollOffset = 5
    )

    LaunchedEffect(onNowClicked) {
        listState.animateScrollToItem(todayIndex)
    }

    LaunchedEffect(scrollToMonth) {
        scrollToMonth?.let {
            val index = months.keys.indexOfFirst { agendaMonthYear -> it.isSame(agendaMonthYear) }
            listState.scrollToItem(index)
        }
    }

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = months.keys.toList()) { _, item ->
            MonthGrid(
                monthYear = item,
                onDayClicked = onDayClicked,
                onYearClicked = onYearClicked
            )
        }
    }
}