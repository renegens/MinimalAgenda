package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import presentation.screens.calendar.CalendarContract.Section
import presentation.theme.CustomColor
import presentation.theme.DMSans18Regular
import presentation.theme.DMSansBlack36

//can be deleted
@Composable
fun ActionButton(
    currentSection: Section,
    nowClicked: () -> Unit,
    toggleActive: () -> Unit,
    onAdd: () -> Unit,
    showAll: Boolean,
) {
    // This component is deprecated and can be removed
    return
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.padding(end = 16.dp)) {
        Box(
            modifier = Modifier.size(56.dp)
                .background(color = CustomColor.Black_500, shape = CircleShape).clickable {
                    haptic.addHapticFeedback()
                    toggleActive.invoke()
                }, contentAlignment = Alignment.Center
        ) {
            val label = if (showAll) "Active" else "All"
            Text(
                label.uppercase(),
                color = CustomColor.White_500,
                style = DMSansBlack36,
                modifier = Modifier.wrapContentHeight(unbounded = true)
            )
        }
        Box(
            modifier = Modifier.size(56.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                .clickable {
                    haptic.addHapticFeedback()
                    nowClicked.invoke()
                }, contentAlignment = Alignment.Center

        ) {
            Text(
                "Now".uppercase(),
                color = CustomColor.White_500,
                style = DMSansBlack36,
                modifier = Modifier.wrapContentHeight(unbounded = true)
            )
        }
        Box(
            modifier = Modifier.size(56.dp)
                .background(color = CustomColor.Black_500, shape = CircleShape).clickable {
                    haptic.addHapticFeedback()
                    onAdd.invoke()
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "+",
                color = CustomColor.White_500,
                style = DMSans18Regular,
                modifier = Modifier.wrapContentHeight(unbounded = true).offset(y = (-2).dp)
            )
        }
    }
}