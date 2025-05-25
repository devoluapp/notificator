package devoluapp.github.io.notificator.worker

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.content.res.AppCompatResources
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import devoluapp.github.io.feat_notificador.R
import java.util.concurrent.TimeUnit

internal class NotificationScheduler(private val context: Context) {
    
    companion object {
        private const val WORK_NAME = "notification_work"
    }

    fun scheduleNotifications(startTime: String, endTime: String, interval: Int, iconResId: Int?) {
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
            interval.toLong(), TimeUnit.MINUTES,
            interval.toLong(), TimeUnit.MINUTES // Flexibilidade igual ao intervalo
        )
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
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