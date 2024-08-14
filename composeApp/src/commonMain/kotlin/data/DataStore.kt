package data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class DataStore {
    val preferences: DataStore<Preferences>
}

internal const val DATA_STORE_FILE_NAME = "prefs .preferences_pb"