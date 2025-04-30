package devoluapp.github.io.notificador.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings")

class NotificationPreferences(private val context: Context) {
    
    companion object {
        private val START_TIME = stringPreferencesKey("start_time")
        private val END_TIME = stringPreferencesKey("end_time")
        const val DEFAULT_START_TIME = "08:00"
        const val DEFAULT_END_TIME = "20:00"
    }

    val startTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[START_TIME] ?: DEFAULT_START_TIME
    }

    val endTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[END_TIME] ?: DEFAULT_END_TIME
    }

    suspend fun saveStartTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[START_TIME] = time
        }
    }

    suspend fun saveEndTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[END_TIME] = time
        }
    }
} 