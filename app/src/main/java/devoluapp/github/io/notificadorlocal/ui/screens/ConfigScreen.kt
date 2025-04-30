package devoluapp.github.io.notificadorlocal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import devoluapp.github.io.notificadorlocal.data.NotificationPreferences
import devoluapp.github.io.notificadorlocal.worker.NotificationScheduler
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen() {
    val context = LocalContext.current
    val preferences = remember { NotificationPreferences(context) }
    val scheduler = remember { NotificationScheduler(context) }
    
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("20:00") }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var newStartTime by remember { mutableStateOf<String?>(null) }
    var newEndTime by remember { mutableStateOf<String?>(null) }
    
    // Coleta os valores salvos
    LaunchedEffect(Unit) {
        preferences.startTime.collect { startTime = it }
    }
    LaunchedEffect(Unit) {
        preferences.endTime.collect { endTime = it }
    }

    // Salva o novo horário inicial
    LaunchedEffect(newStartTime) {
        newStartTime?.let { time ->
            preferences.saveStartTime(time)
            newStartTime = null
        }
    }

    // Salva o novo horário final
    LaunchedEffect(newEndTime) {
        newEndTime?.let { time ->
            preferences.saveEndTime(time)
            newEndTime = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configuração de Notificações",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Horário de Início",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = startTime)
                }

                Text(
                    text = "Horário de Fim",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = endTime)
                }
            }
        }

        Button(
            onClick = {
                scheduler.scheduleNotifications(startTime, endTime)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Configurações")
        }
    }

    // Time Picker Dialog para horário inicial
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onConfirm = { time ->
                val newTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                startTime = newTime
                newStartTime = newTime
                showStartTimePicker = false
            },
            initialTime = LocalTime.parse(startTime)
        )
    }

    // Time Picker Dialog para horário final
    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onConfirm = { time ->
                val newTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                endTime = newTime
                newEndTime = newTime
                showEndTimePicker = false
            },
            initialTime = LocalTime.parse(endTime)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    initialTime: LocalTime
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        },
        title = { Text("Selecione o horário") },
        text = {
            TimePicker(
                state = timePickerState
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    state: TimePickerState,
    onTimeChange: (Int, Int) -> Unit
) {
    LaunchedEffect(state) {
        state.hour.let { hour ->
            state.hour = hour
        }
    }

    LaunchedEffect(state) {
        state.minute.let { minute ->
            state.minute = minute
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Seletor de Hora
        OutlinedTextField(
            value = state.hour.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { hour ->
                    state.hour = hour
                    onTimeChange(hour, state.minute)
                }
            },
            label = { Text("Hora") },
            modifier = Modifier.width(96.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Text(":")

        // Seletor de Minuto
        OutlinedTextField(
            value = state.minute.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { minute ->
                    state.minute = minute
                    onTimeChange(state.hour, minute)
                }
            },
            label = { Text("Minuto") },
            modifier = Modifier.width(96.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
} 