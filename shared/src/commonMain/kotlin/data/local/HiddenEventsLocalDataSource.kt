package data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HiddenEventsLocalDataSource(private val dataStore: DataStore) {

    companion object {
        const val HIDDEN_EVENTS = "hidden events"
        const val EMPTY_JSON_ARRAY = "[]"
    }

    suspend fun getEvents(): String {
        return dataStore.preferences.data.catch {
            ""
        }.map {
            val events = it[stringPreferencesKey(HIDDEN_EVENTS)] ?: ""
            return@map events
        }.first()
    }

    suspend fun saveEvents(events: String) {
        dataStore.preferences.edit {
            it[stringPreferencesKey(HIDDEN_EVENTS)] = events
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}