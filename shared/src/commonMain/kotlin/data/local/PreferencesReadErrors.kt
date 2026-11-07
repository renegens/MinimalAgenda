package data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import okio.IOException

/** Emit a fallback instead of completing without a value; cancellation and programming errors propagate. */
internal fun Flow<Preferences>.recoverReadErrors(): Flow<Preferences> = catch { error ->
    if (error is IOException) emit(emptyPreferences()) else throw error
}
