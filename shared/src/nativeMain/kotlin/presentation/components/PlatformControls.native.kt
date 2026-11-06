package presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import com.slapps.cupertino.CupertinoDropdownMenu
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.MenuAction
import com.slapps.cupertino.CupertinoMenuScope
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.filled.LockCircle
import com.slapps.cupertino.icons.outlined.Bell
import com.slapps.cupertino.icons.outlined.CalendarBadgePlus
import com.slapps.cupertino.icons.outlined.Clock
import com.slapps.cupertino.icons.outlined.Ellipsis
import com.slapps.cupertino.icons.outlined.Eye
import com.slapps.cupertino.icons.outlined.EyeSlash
import com.slapps.cupertino.icons.outlined.Gear
import com.slapps.cupertino.icons.outlined.InfoCircle
import com.slapps.cupertino.icons.outlined.Pencil
import com.slapps.cupertino.icons.outlined.Plus
import com.slapps.cupertino.theme.CupertinoTheme

private val LocalMenuScope = staticCompositionLocalOf<CupertinoMenuScope> {
    error("Menu actions must be inside AppDropdownMenu")
}

@Composable
actual fun AppIcon(symbol: AppSymbol, contentDescription: String?, modifier: Modifier, tint: Color) {
    val vector = when (symbol) {
        AppSymbol.Menu -> CupertinoIcons.Outlined.Ellipsis
        AppSymbol.Visibility -> CupertinoIcons.Default.Eye
        AppSymbol.VisibilityOff -> CupertinoIcons.Default.EyeSlash
        AppSymbol.Clock -> CupertinoIcons.Default.Clock
        AppSymbol.Add -> CupertinoIcons.Default.Plus
        AppSymbol.Settings -> CupertinoIcons.Default.Gear
        AppSymbol.Reminder -> CupertinoIcons.Default.Bell
        AppSymbol.Info -> CupertinoIcons.Default.InfoCircle
        AppSymbol.Edit -> CupertinoIcons.Default.Pencil
        AppSymbol.CalendarAdd -> CupertinoIcons.Outlined.CalendarBadgePlus
        AppSymbol.PrivacyLock -> CupertinoIcons.Filled.LockCircle
    }
    CupertinoIcon(vector, contentDescription, modifier, tint)
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
actual fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
    containerColor: Color,
    content: @Composable () -> Unit,
) {
    CupertinoTheme {
        CupertinoDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = offset,
            containerColor = containerColor,
        ) {
            CompositionLocalProvider(LocalMenuScope provides this) { content() }
        }
    }
}

@Composable
actual fun AppMenuAction(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    with(LocalMenuScope.current) {
        MenuAction(onClick = onClick, icon = icon, enabled = enabled) { content() }
    }
}
