package data.reminder

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import data.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Persists scheduled reminders as a JSON list in the app DataStore (same approach as hidden events). */
class ScheduledReminderStore(private val dataStore: DataStore) {

    private val key = stringPreferencesKey(KEY)
    private val serializer = ListSerializer(ScheduledReminder.serializer())

    suspend fun getAll(): List<ScheduledReminder> =
        decode(dataStore.preferences.data.first()[key])

    suspend fun get(id: String): ScheduledReminder? = getAll().firstOrNull { it.id == id }

    /** Adds or replaces the reminder with the same id. */
    suspend fun save(reminder: ScheduledReminder) {
        dataStore.preferences.edit { prefs ->
            val updated = decode(prefs[key]).filterNot { it.id == reminder.id } + reminder
            prefs[key] = Json.encodeToString(serializer, updated)
        }
    }

    suspend fun remove(id: String) {
        dataStore.preferences.edit { prefs ->
            prefs[key] = Json.encodeToString(serializer, decode(prefs[key]).filterNot { it.id == id })
        }
    }

    private fun decode(raw: String?): List<ScheduledReminder> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            Json.decodeFromString(serializer, raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private companion object {
        const val KEY = "scheduled reminders"
    }
}
