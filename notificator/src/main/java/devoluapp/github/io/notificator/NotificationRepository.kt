package devoluapp.github.io.notificator

interface NotificationRepository {
    suspend fun getNotificationText() : String
}