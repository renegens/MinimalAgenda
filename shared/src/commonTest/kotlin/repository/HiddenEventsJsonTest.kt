package repository

import data.local.HiddenEventEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class HiddenEventsJsonTest {
    @Test fun malformedStoredVisibilityDoesNotPreventLoadingCalendars() {
        for (value in listOf("", " ", "{", "null", "[{}]")) {
            assertEquals(emptyList(), HiddenEventsJson.decode(value))
        }
    }

    @Test fun existingFormatAndUnknownFieldsRemainReadable() {
        val event = HiddenEventEntity("12/123000", 20000)
        assertEquals(listOf(event), HiddenEventsJson.decode(HiddenEventsJson.encode(listOf(event))))
        assertEquals(listOf(event), HiddenEventsJson.decode("""[{"eventId":"12/123000","epochDay":20000,"future":true}]"""))
    }
}
