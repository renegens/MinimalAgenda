package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoDropdownMenu
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.MenuAction
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.outlined.Clock
import com.slapps.cupertino.icons.outlined.Ellipsis
import com.slapps.cupertino.icons.outlined.Eye
import com.slapps.cupertino.icons.outlined.EyeSlash
import com.slapps.cupertino.icons.outlined.Gear
import com.slapps.cupertino.icons.outlined.Plus
import com.slapps.cupertino.theme.CupertinoTheme


@Composable
fun SimpleTransparentToolbar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    onMenuClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .statusBarsPadding()
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        content()

        // Add three-dot menu button with blurred background
        if (onMenuClick != null) {
            val haptic = LocalHapticFeedback.current

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .clickable {
                        haptic.addHapticFeedback()
                        onMenuClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(
                    imageVector = CupertinoIcons.Outlined.Ellipsis,
                    contentDescription = "Menu",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CalendarToolbarMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    showAll: Boolean,
    onNowClicked: () -> Unit,
    onToggleActive: () -> Unit,
    onAdd: () -> Unit,
    onSettings: () -> Unit,
) {
    if (!isVisible) return

    val haptic = LocalHapticFeedback.current

    CupertinoTheme {
        CupertinoDropdownMenu(
            offset = DpOffset(100.dp, -(4).dp),
            expanded = isVisible,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            onDismissRequest = {
                haptic.addHapticFeedbackProminent()
                onDismiss()
            }
        ) {
            // Removed FloatingBackground, just use Column directly
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                MenuAction(
                    onClick = {
                        haptic.addHapticFeedbackProminent()
                        onToggleActive()
                        onDismiss()
                    },
                    icon = {
                        val image = if (showAll) {
                            CupertinoIcons.Default.Eye
                        } else {
                            CupertinoIcons.Default.EyeSlash
                        }
                        CupertinoIcon(
                            imageVector = image,
                            contentDescription = "toggle visibility",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    val text = if (showAll) "Hide hidden events" else "Show hidden events"
                    Label(text)
                }
                HorizontalDivider()
                MenuAction(
                    onClick = {
                        haptic.addHapticFeedbackProminent()
                        onNowClicked()
                        onDismiss()
                    },
                    icon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Clock,
                            contentDescription = "now",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    Label("Today")
                }
                HorizontalDivider()
                MenuAction(
                    onClick = {
                        haptic.addHapticFeedbackProminent()
                        onAdd()
                        onDismiss()
                    },
                    icon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Plus,
                            contentDescription = "add",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    Label("New Event")
                }
                HorizontalDivider()
                MenuAction(
                    onClick = {
                        haptic.addHapticFeedbackProminent()
                        onSettings()
                        onDismiss()
                    },
                    icon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Gear,
                            contentDescription = "settings",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    Label("Settings")
                }
            }
        }
    }
}
