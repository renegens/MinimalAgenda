package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.AgendaEvent
import com.slapps.cupertino.CupertinoDropdownMenu
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.MenuAction
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.outlined.Bell
import com.slapps.cupertino.icons.outlined.Eye
import com.slapps.cupertino.icons.outlined.EyeSlash
import com.slapps.cupertino.icons.outlined.Pencil
import com.slapps.cupertino.theme.CupertinoTheme
import presentation.theme.DMSans18Medium

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun EventOptionsMenu(
    event: AgendaEvent,
    onNotify: (AgendaEvent) -> Unit,
    onHide: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    selectedItem: AgendaEvent?,
    onItemSelected: (AgendaEvent?) -> Unit,
    hapticFeedback: HapticFeedback,
    isDemoMode: Boolean
) {
    CupertinoTheme {
        CupertinoDropdownMenu(
            offset = DpOffset(16.dp, -(4).dp),
            expanded = selectedItem == event,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            onDismissRequest = {
                hapticFeedback.addHapticFeedbackProminent()
                onItemSelected.invoke(null)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                MenuAction(
                    onClick = {
                        onHide.invoke(event)
                        onItemSelected.invoke(null)
                    },
                    icon = {
                        val image = if (event.isHidden) {
                            CupertinoIcons.Default.Eye
                        } else {
                            CupertinoIcons.Default.EyeSlash
                        }
                        CupertinoIcon(
                            imageVector = image,
                            contentDescription = "eye",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    val text = if (event.isHidden) {
                        "Show"
                    } else {
                        "Hide"
                    }
                    Label(text)
                }
                HorizontalDivider()
                MenuAction(
                    onClick = {
                        onNotify.invoke(event)
                        hapticFeedback.addHapticFeedbackProminent()
                        onItemSelected.invoke(null)
                    },
                    icon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Bell,
                            contentDescription = "bell",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                ) {
                    Label("Remind me")
                }
                HorizontalDivider()
                MenuAction(
                    onClick = {
                        hapticFeedback.addHapticFeedbackProminent()
                        onEdit.invoke(event)
                        onItemSelected.invoke(null)
                    },
                    icon = {
                        CupertinoIcon(
                            imageVector = CupertinoIcons.Default.Pencil,
                            contentDescription = "pencil",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    enabled = isDemoMode.not()
                ) {
                    Text(
                        text = "Edit",
                        style = DMSans18Medium.copy(fontSize = 17.sp),
                        color = if (isDemoMode) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5F) else MaterialTheme.colorScheme.tertiary,
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