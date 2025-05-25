package devoluapp.github.io.notificdorclient.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import devoluapp.github.io.notificator.Notificator
import devoluapp.github.io.notificdorclient.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

// Cores personalizadas
private val SalmonLight = Color(0xFFFFE4E1) // Salmon claro
private val SalmonMedium = Color(0xFFFA8072) // Salmon médio
private val SalmonDark = Color(0xFFE9967A) // Salmon escuro
private val BrownText = Color(0xFF4A2C2A) // Marrom escuro para texto
private val BrownBorder = Color(0xFF8B4513) // Marrom para bordas
private val ErrorColor = Color(0xFFE74C3C) // Vermelho para erros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    notificator: Notificator,
    modifier: Modifier = Modifier
) {
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("20:00") }
    var interval by remember { mutableStateOf(15) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showIntervalPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Observar as preferências atuais
    val currentStartTime by notificator.startTime.collectAsState(initial = "08:00")
    val currentEndTime by notificator.endTime.collectAsState(initial = "20:00")
    val currentInterval by notificator.interval.collectAsState(initial = 15)

    // Atualizar estados locais quando as preferências mudarem
    LaunchedEffect(currentStartTime) {
        startTime = currentStartTime
    }

    LaunchedEffect(currentEndTime) {
        endTime = currentEndTime
    }

    LaunchedEffect(currentInterval) {
        interval = currentInterval
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SalmonLight),
        color = SalmonLight
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Notificações",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = BrownText
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = SalmonLight,
                    contentColor = BrownText
                ),
                border = CardDefaults.outlinedCardBorder()
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
                        style = MaterialTheme.typography.titleMedium,
                        color = BrownText
                    )

                    OutlinedButton(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrownBorder,
                            containerColor = SalmonLight
                        )
                    ) {
                        Text(
                            text = startTime,
                            style = MaterialTheme.typography.titleLarge,
                            color = BrownText
                        )
                    }

                    Text(
                        text = "Horário de Fim",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = BrownText
                    )

                    OutlinedButton(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrownBorder,
                            containerColor = SalmonLight
                        )
                    ) {
                        Text(
                            text = endTime,
                            style = MaterialTheme.typography.titleLarge,
                            color = BrownText
                        )
                    }

                    Text(
                        text = "Intervalo entre Notificações",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = BrownText
                    )

                    OutlinedButton(
                        onClick = { showIntervalPicker = true },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrownBorder,
                            containerColor = SalmonLight
                        )
                    ) {
                        Text(
                            text = formatInterval(interval),
                            style = MaterialTheme.typography.titleLarge,
                            color = BrownText
                        )
                    }
                }
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        try {
                            // Salvar as configurações
                            notificator.saveStartTime(startTime)
                            notificator.saveEndTime(endTime)
                            notificator.saveInterval(interval)
                            
                            // Agendar as notificações
                            notificator.scheduleNotifications(
                                startTime = startTime,
                                endTime = endTime,
                                interval = interval,
                                iconResId = R.drawable.ic_notification_customized
                            )
                            
                            errorMessage = null
                        } catch (e: IllegalArgumentException) {
                            errorMessage = e.message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SalmonMedium,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Salvar Configurações",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    // Time Picker Dialog para horário inicial
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onConfirm = { time ->
                startTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
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
                endTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                showEndTimePicker = false
            },
            initialTime = LocalTime.parse(endTime)
        )
    }

    // Interval Picker Dialog
    if (showIntervalPicker) {
        IntervalPickerDialog(
            onDismissRequest = { showIntervalPicker = false },
            onIntervalSelected = { newInterval ->
                interval = newInterval
                showIntervalPicker = false
            },
            currentInterval = interval,
            startTime = LocalTime.parse(startTime),
            endTime = LocalTime.parse(endTime)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
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
        containerColor = SalmonLight,
        title = { Text("Selecione o horário", color = BrownText) },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = SalmonLight,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = BrownText,
                    selectorColor = SalmonMedium,
                    containerColor = SalmonLight,
                    periodSelectorBorderColor = BrownBorder,
                    periodSelectorSelectedContainerColor = SalmonMedium,
                    periodSelectorUnselectedContainerColor = SalmonLight,
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorUnselectedContentColor = BrownText,
                    timeSelectorSelectedContainerColor = SalmonMedium,
                    timeSelectorUnselectedContainerColor = SalmonLight,
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = BrownText
                )
            )
        },
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
                Text("OK", color = BrownBorder)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = BrownText)
            }
        }
    )
}

@Composable
private fun IntervalPickerDialog(
    onDismissRequest: () -> Unit,
    onIntervalSelected: (Int) -> Unit,
    currentInterval: Int,
    startTime: LocalTime,
    endTime: LocalTime
) {
    val totalMinutes = java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime)
    val maxInterval = totalMinutes.toInt()
    
    // Intervalos predefinidos em minutos
    val predefinedIntervals = listOf(
        15, 30, 45, // Intervalos de 15, 30 e 45 minutos
        60, 120, 180, 240, 300, 360 // Intervalos de 1 a 6 horas
    ).filter { it <= maxInterval }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = SalmonLight,
        title = { Text("Selecione o intervalo", color = BrownText) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                predefinedIntervals.forEach { minutes ->
                    val hours = minutes / 60
                    val remainingMinutes = minutes % 60
                    val intervalText = when {
                        hours > 0 && remainingMinutes > 0 -> "$hours hora(s) e $remainingMinutes minuto(s)"
                        hours > 0 -> "$hours hora(s)"
                        else -> "$minutes minuto(s)"
                    }

                    OutlinedButton(
                        onClick = { onIntervalSelected(minutes) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        enabled = minutes >= 15 && minutes <= maxInterval,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrownBorder,
                            containerColor = SalmonLight
                        )
                    ) {
                        Text(
                            text = intervalText,
                            style = MaterialTheme.typography.titleMedium,
                            color = BrownText
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar", color = BrownText)
            }
        }
    )
}

private fun formatInterval(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours > 0 && remainingMinutes > 0 -> "$hours hora(s) e $remainingMinutes minuto(s)"
        hours > 0 -> "$hours hora(s)"
        else -> "$minutes minuto(s)"
    }
}

