package presentation.components.years

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDate
import presentation.theme.Header1
import use_case.getMonths

@Composable
fun YearsScreen(years: Map<Int, List<LocalDate>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        items(years.entries.size) { index ->
            Divider(color = Color.White)
            val year = years.entries.elementAt(index)
            Text(year.key.toString(), style = Header1, color = Color.White)
            Months()
        }
    }
}