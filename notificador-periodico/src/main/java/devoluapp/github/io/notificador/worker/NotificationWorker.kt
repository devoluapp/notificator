package devoluapp.github.io.notificador.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import devoluapp.github.io.feat_notificador.R
import devoluapp.github.io.notificador.data.NotificationServiceLocator
import java.time.LocalTime

class NotificationWorker (
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_ID = 1
        const val START_TIME_KEY = "start_time"
        const val END_TIME_KEY = "end_time"
        const val ICON_RES_ID = "icon_res_id"
    }

    override suspend fun doWork(): Result {
        val startTimeStr = inputData.getString(START_TIME_KEY) ?: return Result.failure()
        val endTimeStr = inputData.getString(END_TIME_KEY) ?: return Result.failure()
        var iconResId = inputData.getInt(ICON_RES_ID, R.drawable.ic_notification_default)
        Log.d("Afirme!", "doWork -> iconResId => $iconResId")
        val currentTime = LocalTime.now()
        val startTime = LocalTime.parse(startTimeStr)
        val endTime = LocalTime.parse(endTimeStr)

        // Verifica se o horário atual está dentro do intervalo configurado
        if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
            showNotification(iconResId)
        }

        return Result.success()
    }

    private suspend fun showNotification(iconResId : Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Criar canal de notificação para Android O e superior
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Notificações Locais",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para notificações locais periódicas"
        }
        notificationManager.createNotificationChannel(channel)

        val textoNotificacao = NotificationServiceLocator.repository?.geraTextoAfirmacao()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("")
            .setContentText("$textoNotificacao")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$textoNotificacao"))
            .setSmallIcon(iconResId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
} 