package presentation.components

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.addHapticFeedback() {
    performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

fun HapticFeedback.addHapticFeedbackProminent() {
    performHapticFeedback(HapticFeedbackType.LongPress)
}