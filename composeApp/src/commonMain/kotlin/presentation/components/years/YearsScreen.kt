package presentation.components.years

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.YearData
import kotlinx.datetime.LocalDate

@Composable
fun YearsScreen(years: List<YearData>, today: LocalDate, todayIndex: Int ) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex + 1, initialFirstVisibleItemScrollOffset = 5)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(top = 56.dp, bottom = 70.dp)
    ) {
        itemsIndexed(
            items = years,
            key = { _, year -> year.year }
        ) { index, year ->
            Divider(color = Color.White)
            Text(year.year.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Months(renderingYear = year.year, today = today)
        }
    }
}