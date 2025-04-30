package devoluapp.github.io.notificator

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import devoluapp.github.io.notificator.data.NotificationPreferences
import devoluapp.github.io.notificator.data.NotificationServiceLocator
import devoluapp.github.io.notificator.worker.NotificationScheduler

class Notificator(context: Context) {
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
        fun solicitarPermissaoDeNotificacao(
            activity: ComponentActivity,
            onResult: (Boolean) -> Unit = {}
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val launcher = activity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    onResult(isGranted)
                }
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onResult(true) // Permissão sempre "concedida" para versões < 13
            }
        }
    }

}