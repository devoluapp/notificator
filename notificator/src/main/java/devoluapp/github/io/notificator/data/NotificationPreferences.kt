package devoluapp.github.io.notificator.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.temporal.ChronoUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings")

internal class NotificationPreferences(private val context: Context) {
    
    companion object {
        private val START_TIME = stringPreferencesKey("start_time")
        private val END_TIME = stringPreferencesKey("end_time")
        private val INTERVAL = intPreferencesKey("interval")
        const val DEFAULT_START_TIME = "08:00"
        const val DEFAULT_END_TIME = "20:00"
        const val DEFAULT_INTERVAL = 15
        const val MIN_INTERVAL = 15
    }

    val startTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[START_TIME] ?: DEFAULT_START_TIME
    }

    val endTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[END_TIME] ?: DEFAULT_END_TIME
    }

    val interval: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[INTERVAL] ?: DEFAULT_INTERVAL
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

    suspend fun saveInterval(minutes: Int) {
        if (minutes < MIN_INTERVAL) {
            throw IllegalArgumentException("O intervalo não pode ser menor que $MIN_INTERVAL minutos")
        }

        // Verifica se o intervalo é menor que o tempo total entre início e fim
        val startTimeStr = startTime.first()
        val endTimeStr = endTime.first()
        
        val startTime = LocalTime.parse(startTimeStr)
        val endTime = LocalTime.parse(endTimeStr)
        val totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime)
        
        if (minutes > totalMinutes) {
            throw IllegalArgumentException("O intervalo não pode ser maior que o tempo total entre início e fim ($totalMinutes minutos)")
        }

        context.dataStore.edit { preferences ->
            preferences[INTERVAL] = minutes
        }
    }
} 