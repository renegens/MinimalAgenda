package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.alexzhirkevich.cupertino.CupertinoDropdownMenu
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.MenuAction
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Clock
import io.github.alexzhirkevich.cupertino.icons.outlined.Ellipsis
import io.github.alexzhirkevich.cupertino.icons.outlined.Eye
import io.github.alexzhirkevich.cupertino.icons.outlined.EyeSlash
import io.github.alexzhirkevich.cupertino.icons.outlined.Gear
import io.github.alexzhirkevich.cupertino.icons.outlined.Plus
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme

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

@OptIn(ExperimentalCupertinoApi::class)
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
    
    Popup(
        onDismissRequest = {
            haptic.addHapticFeedbackProminent()
            onDismiss()
        },
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            // Position the menu at the top-right, accounting for status bar and toolbar height
            Box(
                modifier = Modifier
                    .padding(top = 88.dp, end = 16.dp), // 56dp toolbar + 32dp status bar
                contentAlignment = Alignment.TopEnd
            ) {
                CupertinoTheme {
                    CupertinoDropdownMenu(
                        offset = DpOffset(0.dp, 8.dp),
                        expanded = isVisible,
                        onDismissRequest = {
                            haptic.addHapticFeedbackProminent()
                            onDismiss()
                        }
                    ) {
                        FloatingBackground {
                            Column(modifier = Modifier.fillMaxSize()) {
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
                                    val text = if (showAll) "Show Active" else "Show All"
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
            }
        }
    }
}