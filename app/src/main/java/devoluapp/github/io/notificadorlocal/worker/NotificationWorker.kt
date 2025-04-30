package devoluapp.github.io.notificadorlocal.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import devoluapp.github.io.notificadorlocal.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_ID = 1
        const val START_TIME_KEY = "start_time"
        const val END_TIME_KEY = "end_time"
    }

    override suspend fun doWork(): Result {
        val startTimeStr = inputData.getString(START_TIME_KEY) ?: return Result.failure()
        val endTimeStr = inputData.getString(END_TIME_KEY) ?: return Result.failure()
        
        val currentTime = LocalTime.now()
        val startTime = LocalTime.parse(startTimeStr)
        val endTime = LocalTime.parse(endTimeStr)

        // Verifica se o horário atual está dentro do intervalo configurado
        if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Criar canal de notificação para Android O e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificações Locais",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificações locais periódicas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Lembrete")
            .setContentText("Hora do seu lembrete: $currentTime")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
} 