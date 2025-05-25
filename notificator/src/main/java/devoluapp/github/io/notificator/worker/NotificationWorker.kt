package devoluapp.github.io.notificator.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import devoluapp.github.io.feat_notificador.R
import devoluapp.github.io.notificator.data.NotificationServiceLocator
import devoluapp.github.io.notificator.receiver.NotificationTextReceiver
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.System.currentTimeMillis
import java.time.LocalTime
import kotlin.coroutines.resume

internal class NotificationWorker (
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_GROUP_KEY = "notification_group"
        const val SUMMARY_NOTIFICATION_ID = 0
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

        // Tenta obter o texto do repositório
        var textoNotificacao = NotificationServiceLocator.repository?.getNotificationText()

        // Se o repositório não estiver disponível, solicita o texto via broadcast
        if (textoNotificacao == null) {
            textoNotificacao = requestNotificationText()
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("")
            .setContentText(textoNotificacao)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textoNotificacao))
            .setSmallIcon(iconResId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .build()

        notificationManager.notify(currentTimeMillis().toInt(), notification)

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Afirmações")
            .setContentText("Você tem novas afirmações")
            .setSmallIcon(iconResId)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setGroupSummary(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("Afirmação 1")
                    .addLine("Afirmação 2")
                // Você pode popular dinamicamente as linhas se quiser
            )
            .build()

        notificationManager.notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
    }

    private suspend fun requestNotificationText(): String = suspendCancellableCoroutine { continuation ->
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val text = intent.getStringExtra(NotificationTextReceiver.EXTRA_NOTIFICATION_TEXT)
                if (text != null) {
                    continuation.resume(text)
                } else {
                    continuation.resume("Lembre-se de respirar e manter a calma")
                }
                context.unregisterReceiver(this)
            }
        }

        // Registra o receiver temporariamente
        context.registerReceiver(
            receiver,
            android.content.IntentFilter(NotificationTextReceiver.ACTION_GET_NOTIFICATION_TEXT)
        )

        // Envia o broadcast para solicitar o texto
        val intent = Intent(NotificationTextReceiver.ACTION_GET_NOTIFICATION_TEXT)
        context.sendBroadcast(intent)

        // Timeout de 5 segundos
        android.os.Handler(context.mainLooper).postDelayed({
            try {
                context.unregisterReceiver(receiver)
                if (continuation.isActive) {
                    continuation.resume("Lembre-se de respirar e manter a calma")
                }
            } catch (e: Exception) {
                // Ignora exceções ao desregistrar o receiver
            }
        }, 5000)
    }
} 