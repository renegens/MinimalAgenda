package data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LaunchCounterDataSource(private val dataStore: DataStore) {

    companion object {
        const val COUNTER = "launch counter"
    }

    suspend fun getCount(): Int {
        return dataStore.preferences.data.catch {
           0
        }.map {
            return@map it[intPreferencesKey(COUNTER)] ?: 0
        }.first()
    }

    suspend fun increment() {
        dataStore.preferences.updateData { preferences ->
            val currentCounter = preferences[intPreferencesKey(COUNTER)] ?: 0
            preferences.toMutablePreferences().apply {
                this[intPreferencesKey(COUNTER)] = currentCounter + 1
            }
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}