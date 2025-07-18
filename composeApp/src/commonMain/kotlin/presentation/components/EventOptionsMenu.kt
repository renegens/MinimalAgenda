package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import domain.model.AppThemeMode
import io.github.alexzhirkevich.cupertino.CupertinoDropdownMenu
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.MenuAction
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.Bell
import io.github.alexzhirkevich.cupertino.icons.outlined.Eye
import io.github.alexzhirkevich.cupertino.icons.outlined.EyeSlash
import io.github.alexzhirkevich.cupertino.icons.outlined.Pencil
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
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
    appThemeMode: AppThemeMode,
    isDemoMode: Boolean
) {

    CupertinoTheme {
        CupertinoDropdownMenu(
            offset = DpOffset(16.dp, -(4).dp),
            expanded = selectedItem == event,
            onDismissRequest = {
                hapticFeedback.addHapticFeedbackProminent()
                onItemSelected.invoke(null)
            }
        ) {
          PaperBackground(appThemeMode = appThemeMode) {
                Column(modifier = Modifier.fillMaxSize()) {
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
}


@Composable
fun Label(text: String) {
    Text(
        text = text,
        style = DMSans18Medium.copy(fontSize = 17.sp),
        color = MaterialTheme.colorScheme.tertiary,
    )
}