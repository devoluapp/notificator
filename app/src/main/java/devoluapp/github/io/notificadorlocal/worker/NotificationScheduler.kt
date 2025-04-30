package devoluapp.github.io.notificadorlocal.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {
    
    companion object {
        private const val WORK_NAME = "notification_work"
    }

    fun scheduleNotifications(startTime: String, endTime: String) {
        // Cancela todos os trabalhos anteriores
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        // Cria os dados de entrada para o Worker
        val inputData = workDataOf(
            NotificationWorker.START_TIME_KEY to startTime,
            NotificationWorker.END_TIME_KEY to endTime
        )

        // Configura a restrição para executar apenas quando houver conectividade de rede
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Cria a solicitação de trabalho periódico
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flexibilidade de 15 minutos
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        // Agenda o trabalho como único na fila
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
} 