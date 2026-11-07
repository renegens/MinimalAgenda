package data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import data.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SwipeToolTipDataSource(private val dataStore: DataStore) {

    companion object {
        const val SWIPE_TOOLTIP = "swipe tooltip"
    }

    suspend fun get(): Boolean {
        return dataStore.preferences.data.recoverReadErrors().map {
            return@map it[booleanPreferencesKey(SWIPE_TOOLTIP)] ?: false
        }.first()
    }

    suspend fun markSeen() {
        dataStore.preferences.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[booleanPreferencesKey(SWIPE_TOOLTIP)] = true
            }
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}