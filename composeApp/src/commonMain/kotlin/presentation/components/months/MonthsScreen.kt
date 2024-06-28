package presentation.components.months

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.MonthData
import extensions.uniqueMonthID
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import presentation.components.day.Entries
import presentation.components.day.Header
import presentation.components.day.HeaderSecondary

@Composable
fun MonthsScreen(months: List<MonthData>, today: LocalDate, todayIndex: Int) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        itemsIndexed(
            items = months,
            key = { _, month -> month.id }
        ) { index, month ->
            MonthGrid(month, isCurrentMonth = month.id == today.uniqueMonthID())
        }
    }

    coroutineScope.launch {
        listState.scrollToItem(todayIndex, scrollOffset = -200)
    }
}