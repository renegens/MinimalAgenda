package presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SectionDivider(isCurrent: Boolean) {
    val current = if (isCurrent) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.secondary
    }
    ZigzagLine(color = current)
}

@Composable
private fun ZigzagLine(color: Color) {
    Canvas(modifier = Modifier.fillMaxWidth().height(8.dp).padding(horizontal = 8.dp)) {
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(0f, size.height / 2)
            var x = 0f
            val step = 25f  // adjust this value for the width of the zigzag
            while (x < size.width) {
                lineTo(x + step / 2, 0f)
                lineTo(x + step, size.height / 2)
                x += step
            }
            //lineTo(size.width, size.height / 2)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 4f) // adjust stroke width as needed
        )
    }
}

