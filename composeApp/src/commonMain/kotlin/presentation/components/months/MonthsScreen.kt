package presentation.components.months

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import use_case.getMonths


@Composable
fun MonthsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        items(getMonths().size) { index ->
            val date = getMonths()[index]
            CalendarGrid(localDate = date)
        }

    }
}