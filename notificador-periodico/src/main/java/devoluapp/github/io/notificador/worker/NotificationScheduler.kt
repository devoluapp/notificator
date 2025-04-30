package devoluapp.github.io.notificador.worker

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.work.*
import devoluapp.github.io.feat_notificador.R
import devoluapp.github.io.notificador.NotificationRepository
import devoluapp.github.io.notificador.data.NotificationServiceLocator
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {
    
    companion object {
        private const val WORK_NAME = "notification_work"
    }

    fun scheduleNotifications(startTime: String, endTime: String, iconResId: Int?) {
        // Cancela todos os trabalhos anteriores
        cancelNotifications()

        val iconResIdVerified = verificaResourceId(iconResId, R.drawable.ic_notification_default)

        // Cria os dados de entrada para o Worker
        val inputData = workDataOf(
            NotificationWorker.START_TIME_KEY to startTime,
            NotificationWorker.END_TIME_KEY to endTime,
            NotificationWorker.ICON_RES_ID to iconResIdVerified
        )

        // Cria a solicitação de trabalho periódico
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flexibilidade de 15 minutos
        )
            .setInputData(inputData)
            .build()

        // Agenda o trabalho como único na fila
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Verifica se o resource id é nulo ou inválido, caso seja, retorna o resource id padrão.
     * Caso seja válido e não nulo, retorna o próprio resource id passado no paremetro iconResId
     */
    private fun verificaResourceId(iconResId: Int?, defaultResId: Int) : Int {
        //Verifica se o resource id é inválido ou nulo. Neste caso será usado o ResId do icone padrão
        var iconResIdVerified = iconResId ?: defaultResId
        try {
            AppCompatResources.getDrawable(context, iconResIdVerified)
        } catch (e: Resources.NotFoundException) {
            iconResIdVerified = defaultResId
        }
        return iconResIdVerified
    }

    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
} 