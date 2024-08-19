package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.theme.CustomColor

@Composable
fun ActionButton(nowClicked: () -> Unit, toggleActive: () -> Unit, onAdd: () -> Unit, showAll: Boolean) {
        Column(modifier = Modifier.padding(end = 16.dp)) {
            Box(
                modifier = Modifier.size(64.dp).background(color = CustomColor.Black_500, shape = CircleShape),
                contentAlignment = Alignment.Center

            ) {
                val label = if (showAll) "Active" else "All"
                TextButton(onClick = toggleActive) {
                    Text(label, color = CustomColor.White_800)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.size(64.dp).background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = nowClicked) {
                    Text("Now", color = CustomColor.White_800)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.size(64.dp).background(color = CustomColor.Black_500, shape = CircleShape),
                contentAlignment = Alignment.Center

            ) {
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CustomColor.White_800)
                }
            }
        }
}