package presentation.components.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward
import presentation.theme.DMSansBlack36

@Composable
fun SwipeToolTip() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = MaterialTheme.colorScheme.inversePrimary,
                    clip = false
                )
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.inversePrimary)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CupertinoIcon(
                    modifier = Modifier.size(8.dp),
                    imageVector = CupertinoIcons.Default.ChevronBackward,
                    contentDescription = "arrow left",
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SWIPE LEFT FOR MORE",
                    style = DMSansBlack36,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}