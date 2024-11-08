package data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PaywallLocalDataSource(private val dataStore: DataStore) {

    companion object {
        const val PAYWALL = "paywall"
    }

    suspend fun getTimestamp(): Long? {
        return dataStore.preferences.data.catch {
           null
        }.map {
            val timestamp = it[longPreferencesKey(PAYWALL)]
            return@map timestamp
        }.first()
    }

    suspend fun save(timestamp: Long) {
        dataStore.preferences.edit {
            it[longPreferencesKey(PAYWALL)] = timestamp
        }
    }

    suspend fun clearConfig() {
        dataStore.preferences.edit {
            it.clear()
        }
    }
}