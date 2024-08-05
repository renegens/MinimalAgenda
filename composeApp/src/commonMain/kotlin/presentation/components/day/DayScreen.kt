package presentation.components.day

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.DayData
import extensions.millisToTime
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import presentation.components.SectionDivider

@Composable
fun DayScreen(
    days: List<DayData>,
    today: LocalDate,
    todayIndex: Int
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayIndex + 1,
        initialFirstVisibleItemScrollOffset = 5
    )
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(
            items = days,
            key = { index, day -> day.epochDay }
        ) { index, day ->

            val isToday = day.epochDay == today.toEpochDays()
            Header(
                day = day.shortDay,
                dayInMonth = day.dayOfMonth,
                isToday = isToday
            )
            Entries(day)
            SectionDivider(isCurrent = isToday)
        }
    }

    coroutineScope.launch {
        //listState.scrollToItem(todayIndex)
    }
}

@Composable
fun Header(day: String, dayInMonth: Int, isToday: Boolean) {
    Text(
        "$day$dayInMonth",
        style = MaterialTheme.typography.headlineLarge,
        color = if (isToday) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    )
}

@Composable
fun Entries(day: DayData) {
    if (day.events.isEmpty()){
        Spacer(modifier = Modifier.height(56.dp))
    }

    day.events.filter { it.epochDay == day.epochDay }.forEach {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                it.startTime?.millisToTime() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                it.title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}