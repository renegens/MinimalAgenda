package presentation.components.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.MonthData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import presentation.theme.Body
import use_case.getDaysInMonthWithZeroes

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthGrid(
    monthData: MonthData,
    isCurrentMonth: Boolean
) {
    MonthHeader(month = monthData.month.name, year = monthData.year, color = Color.White, isCurrentMonth)
    DaysInWeek()
    CalendarDays(monthData.days)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarDays(days: List<Int>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        maxItemsInEachRow = 7
    ) {
        days.forEach {
            if (it == 0) {
                Text(
                    text = it.toString(),
                    style = Body,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.height(44.dp).width(44.dp),
                    color = Color.Transparent
                )
            } else {
                TextButton(onClick = {}, modifier = Modifier.height(44.dp).width(44.dp)) {
                    Text(
                        text = it.toString(),
                        style = Body,
                        textAlign = TextAlign.Start,
                        color = Color.White
                    )
                }
            }
        }
    }
}


