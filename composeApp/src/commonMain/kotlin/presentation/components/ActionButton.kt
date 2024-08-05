package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(nowClicked: () -> Unit, toggleActive: () -> Unit) {
    Box(
        modifier = Modifier.width(100.dp).height(110.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(0.5f).fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = nowClicked) {
                    Text("Now", color = MaterialTheme.colorScheme.surface)
                }
            }
            Box(
                modifier = Modifier.weight(0.5f).fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center

            ) {
                TextButton(onClick = toggleActive) {
                    Text("Active only", color = MaterialTheme.colorScheme.surface)
                }
            }
        }
    }
}