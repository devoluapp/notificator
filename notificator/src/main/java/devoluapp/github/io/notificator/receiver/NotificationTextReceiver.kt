package devoluapp.github.io.notificator.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import devoluapp.github.io.notificator.data.NotificationServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationTextReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_GET_NOTIFICATION_TEXT = "devoluapp.github.io.notificator.ACTION_GET_NOTIFICATION_TEXT"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GET_NOTIFICATION_TEXT) {
            val notificationText = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT)
            if (notificationText != null) {
                // Atualiza o texto no repositório
                CoroutineScope(Dispatchers.IO).launch {
                    NotificationServiceLocator.repository?.updateNotificationText(notificationText)
                }
            }
        }
    }
} 