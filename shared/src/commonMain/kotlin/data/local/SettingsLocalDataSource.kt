package data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsLocalDataSource(private val dataStore: DataStore) {

    companion object {
        const val SETTINGS = "settings"
    }

    suspend fun getSettings(): String {
        return dataStore.preferences.data.catch {
            ""
        }.map {
            val settings = it[stringPreferencesKey(SETTINGS)] ?: ""
            return@map settings
        }.first()
    }

    suspend fun saveSettings(settings: String) {
        dataStore.preferences.edit {
            it[stringPreferencesKey(SETTINGS)] = settings
        }
    }

    fun settings(): Flow<String> {
        return dataStore.preferences.data.catch {
            ""
        }.map {
            val settings = it[stringPreferencesKey(SETTINGS)] ?: ""
            return@map settings
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}