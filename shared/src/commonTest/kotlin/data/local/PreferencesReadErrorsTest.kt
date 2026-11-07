package data.local

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PreferencesReadErrorsTest {
    @Test
    fun ioFailureEmitsAnEmptyValueRatherThanFailingFirstWithNoSuchElement() = runTest {
        val result = flow<Preferences> { throw IOException("unavailable") }.recoverReadErrors().first()
        assertEquals(emptyMap(), result.asMap())
    }

    @Test
    fun cancellationAndProgrammingErrorsRemainVisible() = runTest {
        assertFailsWith<CancellationException> {
            flow<Preferences> { throw CancellationException("cancelled") }.recoverReadErrors().first()
        }
        assertFailsWith<IllegalStateException> {
            flow<Preferences> { error("programming error") }.recoverReadErrors().first()
        }
    }
}
