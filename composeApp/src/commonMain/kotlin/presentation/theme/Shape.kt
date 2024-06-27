package com.daryse.daryse.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(10.dp)
)

sealed class ImageShape {
    class Large(val dimension: Float = 30F) : ImageShape()
    class Small(val dimension: Float = 20F) : ImageShape()

}

val FeedPostAspectRatio = 1.626F