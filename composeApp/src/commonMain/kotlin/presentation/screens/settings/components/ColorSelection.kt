package presentation.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.AppColor

@Composable
internal fun ColorSelection(selectedAppColor: AppColor, onClick: (AppColor) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppColor.entries.toTypedArray().forEach {
            ColorOption(it, onClick = onClick, isSelected = selectedAppColor == it)
        }
    }
}

@Composable
private fun ColorOption(appColor: AppColor, isSelected: Boolean, onClick: (AppColor) -> Unit) {
    val color = if (isSelected) appColor.color else Color.Transparent

    Box(
        modifier = Modifier
            .size(48.dp)
            .border(2.dp, color, CircleShape)
            .clip(CircleShape)
            .clickable { onClick(appColor) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(appColor.color)
        )
    }
}