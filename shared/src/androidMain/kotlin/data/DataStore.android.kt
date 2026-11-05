package data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

actual class DataStore(context: Context) {

    private val dataStore = createDataStore(context)

    private fun createDataStore(context: Context): DataStore<Preferences> {
        return createPath {
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }

    private fun createPath(producePath: () -> String): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath().toPath() }
        )

    actual val preferences: DataStore<Preferences>
        get() = dataStore

}