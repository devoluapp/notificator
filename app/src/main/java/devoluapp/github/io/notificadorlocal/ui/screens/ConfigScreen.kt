package devoluapp.github.io.notificadorlocal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import devoluapp.github.io.notificator.Notificator
import devoluapp.github.io.notificadorlocal.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen() {
    val context = LocalContext.current
    val notificador = remember { Notificator(
        context
    ) }
    
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("20:00") }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var newStartTime by remember { mutableStateOf<String?>(null) }
    var newEndTime by remember { mutableStateOf<String?>(null) }
    // Coleta os valores salvos
    LaunchedEffect(Unit) {
        notificador.startTime.collect { startTime = it }
    }
    LaunchedEffect(Unit) {
        notificador.endTime.collect { endTime = it }
    }

    // Salva o novo horário inicial
    LaunchedEffect(newStartTime) {
        newStartTime?.let { time ->
            notificador.saveStartTime(time)
            newStartTime = null
        }
    }

    // Salva o novo horário final
    LaunchedEffect(newEndTime) {
        newEndTime?.let { time ->
            notificador.saveEndTime(time)
            newEndTime = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(16.dp)
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Configuração de Notificações",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Horário de Início",
                    style = MaterialTheme.typography.titleLarge
                )
                
                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = startTime, style = MaterialTheme.typography.displayLarge,)
                }

                Text(
                    text = "Horário de Fim",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                
                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = endTime, style = MaterialTheme.typography.displayLarge,)
                }
            }
        }

        Button(
            onClick = {
                notificador.scheduleNotifications(startTime, endTime, R.drawable.ic_notification_customized)
            }
        ) {
            Text(text = "Salvar Configurações",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp))
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

