package presentation.components.years

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.Months
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import presentation.theme.Header2

@Composable
fun Months(renderingYear: Int, today: LocalDate) {
    val months = Months.entries.chunked(6)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(0.5f)) {
            months.first().forEachIndexed { index, month ->

                val selectedColor =
                    if (month.ordinal + 1 == today.month.number && today.year == renderingYear) {
                        Color.Red
                    } else {
                        Color.White
                    }

                MonthText(month.nameRes, color = selectedColor)
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            months[1].forEachIndexed { index, month ->
                val selectedColor =
                    if (month.ordinal + 1 == today.month.number && today.year == renderingYear) {
                        Color.Red
                    } else {
                        Color.White
                    }
                MonthText(month.nameRes, color = selectedColor)
            }
        }
    }
}

@Composable
private fun MonthText(text: String, color: Color) {
    Text(text, style = Header2, color = color)

}