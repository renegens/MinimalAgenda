package presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import domain.model.AppThemeMode
import io.github.alexzhirkevich.cupertino.CupertinoDropdownMenu
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.MenuAction
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Ellipsis
import io.github.alexzhirkevich.cupertino.icons.outlined.Plus
import io.github.alexzhirkevich.cupertino.icons.outlined.Clock
import io.github.alexzhirkevich.cupertino.icons.outlined.Eye
import io.github.alexzhirkevich.cupertino.icons.outlined.EyeSlash
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import kotlinx.coroutines.flow.collectLatest
import presentation.screens.calendar.CalendarContract.Section
import presentation.theme.CustomColor
import presentation.theme.DMSans18Medium
import presentation.theme.DMSans18Regular
import presentation.theme.DMSansBlack36

@Composable
fun SimpleTransparentToolbar(
    listState: LazyListState? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    title: String = "",
    onMenuClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableStateOf(0) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "toolbar_alpha"
    )
    
    // Listen to vertical scroll
    LaunchedEffect(listState) {
        listState?.let { state ->
            try {
                snapshotFlow { state.firstVisibleItemScrollOffset }.collectLatest { scrollOffset ->
                    val scrollDelta = scrollOffset - lastScrollOffset
                    if (kotlin.math.abs(scrollDelta) > 10) { // Threshold for scroll detection
                        isVisible = false
                    }
                    lastScrollOffset = scrollOffset
                }
            } catch (_: Exception) {
                // Ignore errors from uninitialized state
            }
        }
    }
    
    // Show toolbar again after a delay
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            kotlinx.coroutines.delay(800) // 0.8 second delay
            isVisible = true
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .statusBarsPadding()
            .alpha(alpha)
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
fun SimpleToolbarWithMenu(
    listState: LazyListState? = null,
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    SimpleTransparentToolbar(
        listState = listState,
        modifier = modifier,
        backgroundColor = Color.Transparent,
        onMenuClick = onMenuClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun CalendarToolbarMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentSection: Section,
    showAll: Boolean,
    onNowClicked: () -> Unit,
    onToggleActive: () -> Unit,
    onAdd: () -> Unit,
    appThemeMode: AppThemeMode
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
                                    Label("Go to Now")
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
                                    Label("Add Event")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolbarDropdownMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentSection: Section,
    showAll: Boolean,
    onNowClicked: () -> Unit,
    onToggleActive: () -> Unit,
    onAdd: () -> Unit
) {
    if (!isVisible) return

    val haptic = LocalHapticFeedback.current

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, end = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            FloatingBackground(
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    // Toggle Active/All button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color = CustomColor.Black_500, shape = CircleShape)
                            .clickable {
                                haptic.addHapticFeedback()
                                onToggleActive()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val label = if (showAll) "A" else "All"
                        Text(
                            label.uppercase(),
                            color = CustomColor.White_500,
                            style = DMSansBlack36.copy(fontSize = 14.sp),
                            modifier = Modifier.wrapContentHeight(unbounded = true)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Now button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable {
                                haptic.addHapticFeedback()
                                onNowClicked()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Now".uppercase(),
                            color = CustomColor.White_500,
                            style = DMSansBlack36.copy(fontSize = 14.sp),
                            modifier = Modifier.wrapContentHeight(unbounded = true)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color = CustomColor.Black_500, shape = CircleShape)
                            .clickable {
                                haptic.addHapticFeedback()
                                onAdd()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+",
                            color = CustomColor.White_500,
                            style = DMSans18Regular.copy(fontSize = 18.sp),
                            modifier = Modifier.wrapContentHeight(unbounded = true)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Label(text: String) {
        Text(
            text = text,
            style = DMSans18Medium.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}