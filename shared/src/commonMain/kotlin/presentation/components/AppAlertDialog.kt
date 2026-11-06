package presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun AppAlertDialog(
    title: String,
    message: String,
    confirmButtonLabel: String = "OK",
    negativeButtonLabel: String? = null,
    onContinueRequest: () -> Unit,
    onNegativeRequest: (() -> Unit)? = null,
)

/** Dismiss a confirmation through its cancel action; informational dialogs use their close action. */
internal fun dialogDismissAction(
    onContinueRequest: () -> Unit,
    onNegativeRequest: (() -> Unit)?,
): () -> Unit = onNegativeRequest ?: onContinueRequest
