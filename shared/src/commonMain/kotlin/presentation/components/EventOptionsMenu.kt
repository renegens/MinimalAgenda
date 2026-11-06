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
import presentation.theme.DMSans18Medium

@Composable
fun EventOptionsMenu(
    event: AgendaEvent,
    onNotify: (AgendaEvent) -> Unit,
    onHide: (AgendaEvent) -> Unit,
    onEdit: (AgendaEvent) -> Unit,
    onDetails: (AgendaEvent) -> Unit,
    selectedItem: AgendaEvent?,
    onItemSelected: (AgendaEvent?) -> Unit,
    hapticFeedback: HapticFeedback,
    isDemoMode: Boolean
) {
    AppDropdownMenu(
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
            AppMenuAction(
                onClick = {
                    onHide.invoke(event)
                    onItemSelected.invoke(null)
                },
                icon = {
                    val image = if (event.isHidden) {
                        AppSymbol.Visibility
                    } else {
                        AppSymbol.VisibilityOff
                    }
                    AppIcon(
                        symbol = image,
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
            AppMenuAction(
                onClick = {
                    onNotify.invoke(event)
                    hapticFeedback.addHapticFeedbackProminent()
                    onItemSelected.invoke(null)
                },
                icon = {
                    AppIcon(
                        symbol = AppSymbol.Reminder,
                        contentDescription = "bell",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            ) {
                Label("Remind me")
            }
            HorizontalDivider()
            AppMenuAction(
                onClick = {
                    hapticFeedback.addHapticFeedbackProminent()
                    onDetails.invoke(event)
                    onItemSelected.invoke(null)
                },
                icon = {
                    AppIcon(
                        symbol = AppSymbol.Info,
                        contentDescription = "details",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            ) {
                Label("Details")
            }
            HorizontalDivider()
            AppMenuAction(
                onClick = {
                    hapticFeedback.addHapticFeedbackProminent()
                    onEdit.invoke(event)
                    onItemSelected.invoke(null)
                },
                icon = {
                    AppIcon(
                        symbol = AppSymbol.Edit,
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


@Composable
fun Label(text: String) {
    Text(
        text = text,
        style = DMSans18Medium.copy(fontSize = 17.sp),
        color = MaterialTheme.colorScheme.tertiary,
    )
}