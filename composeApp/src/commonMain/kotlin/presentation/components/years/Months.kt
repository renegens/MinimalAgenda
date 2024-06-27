package presentation.components.years

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import presentation.theme.Header2

@Composable
fun Months() {
    val firstSemester = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    val secondSemester = listOf("Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(0.5f)) {
            firstSemester.forEach {
                Text(it, style = Header2, color = Color.White)
            }
        }

        Column(modifier = Modifier.weight(0.5f)) {
            secondSemester.forEach {
                Text(it, style = Header2, color = Color.White)
            }
        }
    }
}