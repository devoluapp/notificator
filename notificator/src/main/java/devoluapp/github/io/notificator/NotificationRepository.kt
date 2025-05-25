package devoluapp.github.io.notificator

interface NotificationRepository {
    suspend fun getNotificationText(): String
    suspend fun updateNotificationText(text: String)
}