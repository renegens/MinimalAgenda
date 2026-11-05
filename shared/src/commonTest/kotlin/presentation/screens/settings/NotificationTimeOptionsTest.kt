package presentation.screens.settings

import presentation.screens.settings.components.notificationTimeLabel
import presentation.screens.settings.components.notificationTimeOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationTimeOptionsTest {
    private val presets = listOf(0, 5, 10, 15, 30, 60)

    @Test
    fun everyPresetHasExactlyOneSelectableOptionIncludingAtStart() {
        presets.forEach { selected ->
            val options = notificationTimeOptions(selected)
            assertEquals(presets, options)
            assertEquals(1, options.count { it == selected })
        }
        assertEquals("At start", notificationTimeLabel(0))
        assertEquals("60 min", notificationTimeLabel(60))
    }

    @Test
    fun customStoredOffsetsAreKeptAsAnExtraSelectedOption() {
        for (selected in listOf(45, 10_080, -5)) {
            val options = notificationTimeOptions(selected)
            assertEquals(presets, options.take(presets.size))
            assertEquals(selected, options.last())
            assertEquals(1, options.count { it == selected })
            assertEquals("$selected min", notificationTimeLabel(selected))
        }
    }

    @Test
    fun explicitlySelectingAPresetReplacesTheCustomSelection() {
        val original = 45
        val selected = notificationTimeOptions(original).first { it == 15 }
        val updated = notificationTimeOptions(selected)
        assertTrue(15 in updated)
        assertFalse(original in updated)
        assertEquals(1, updated.count { it == selected })
    }
}
