package presentation.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry

fun Modifier.clickableWithOpaqueText(
    onClick: () -> Unit
): Modifier {
    return this.composed {
        this.clickable(
            enabled = true,
            onClickLabel = null,
            role = null,
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = customRipple(
                color = MaterialTheme.colorScheme.secondaryContainer,
                radius = 1000000.dp,
                bounded = true,
                rippleAlpha = RippleAlpha(
                    pressedAlpha = 0.4f,
                    focusedAlpha = 1f,
                    draggedAlpha = 1f,
                    hoveredAlpha = 1f
                )
            )
        )
    }
}


val enterPush: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(
        animationSpec = tween(
            200, easing = LinearEasing
        )
    ) + slideIntoContainer(animationSpec = tween(200, easing = LinearEasing),
        towards = AnimatedContentTransitionScope.SlideDirection.Up,
        initialOffset = { it })
}

val exitPush: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(animationSpec = tween(300, easing = LinearEasing),
        towards = AnimatedContentTransitionScope.SlideDirection.Down,
        targetOffset = { 0 })
}

val stayPush: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(
        animationSpec = tween(300, easing = LinearEasing),
    )
}