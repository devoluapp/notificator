package devoluapp.github.io.notificador

import android.content.Context
import devoluapp.github.io.notificador.data.NotificationPreferences
import devoluapp.github.io.notificador.data.NotificationServiceLocator
import devoluapp.github.io.notificador.worker.NotificationScheduler

class NotificadorUtils(context: Context) {
    private val notificationScheduler = NotificationScheduler(context)
    private val notificationPreferences = NotificationPreferences(context)

    val startTime = notificationPreferences.startTime
    val endTime = notificationPreferences.endTime

    suspend fun saveStartTime(
        startTime: String
    ) {
        notificationPreferences.saveStartTime(startTime)
    }

    suspend fun saveEndTime(
        endTime: String
    ) {
        notificationPreferences.saveEndTime(endTime)
    }

    fun scheduleNotifications(startTime: String, endTime: String, iconResId: Int?){
        if(NotificationServiceLocator.repository == null){
            throw(IllegalStateException("Repository not set, " + "use NotificadorUtils.setRepository(repository: NotificationRepository) first."))
        }
        notificationScheduler.scheduleNotifications(startTime, endTime, iconResId)
    }

    companion object {
        fun setRepository(repository: NotificationRepository) {
            NotificationServiceLocator.repository = repository
        }
    }
}