package data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HiddenEventsLocalDataSource(private val dataStore: DataStore) {

    companion object {
        const val HIDDEN_EVENTS = "hidden events"
        const val EMPTY_JSON_ARRAY = "[]"
    }

    suspend fun getEvents(): String {
        return dataStore.preferences.data.recoverReadErrors().map {
            val events = it[stringPreferencesKey(HIDDEN_EVENTS)] ?: ""
            return@map events
        }.first()
    }

    /** Transform within DataStore's transaction so simultaneous visibility changes cannot overwrite each other. */
    suspend fun updateEvents(transform: (String) -> String) {
        dataStore.preferences.edit {
            val key = stringPreferencesKey(HIDDEN_EVENTS)
            it[key] = transform(it[key] ?: EMPTY_JSON_ARRAY)
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}