package presentation.components.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.DayData
import data.EventData
import extensions.millisToTime
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(top = 56.dp, bottom = 70.dp)
    ) {

        itemsIndexed(
            items = days,
            key = { index, day -> day.epochDay }
        ) { index, day ->
            Divider(color = Color.White)
            Header(day.month.name, isToday = day.epochDay == today.toEpochDays())
            HeaderSecondary(day.dayOfWeek.name, day.dayOfMonth)
            Entries(day)
        }
    }

    coroutineScope.launch {
        //listState.scrollToItem(todayIndex)
    }
}

@Composable
fun Header(month: String, isToday: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            month,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) Color.Yellow else Color.White
        )
        Text("Today", style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}

@Composable
fun HeaderSecondary(day: String, dayOfMonth: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(day, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        Text(
            dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
fun Entries(day: DayData) {
    day.events.filter { it.epochDay == day.epochDay }.forEach {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                it.startTime?.millisToTime() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(it.title ?: "", style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}