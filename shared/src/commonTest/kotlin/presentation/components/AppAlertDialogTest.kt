package presentation.components

import kotlin.test.Test
import kotlin.test.assertEquals

class AppAlertDialogTest {
    @Test
    fun dismissingAConfirmationCancelsWithoutConfirming() {
        var confirmations = 0
        var cancellations = 0
        val dismiss = dialogDismissAction(
            onContinueRequest = { confirmations++ },
            onNegativeRequest = { cancellations++ },
        )

        dismiss()

        assertEquals(1, cancellations)
        assertEquals(0, confirmations)
    }

    @Test
    fun dismissingAnInformationalDialogClosesIt() {
        var closes = 0
        val dismiss = dialogDismissAction(
            onContinueRequest = { closes++ },
            onNegativeRequest = null,
        )

        dismiss()

        assertEquals(1, closes)
    }
}
