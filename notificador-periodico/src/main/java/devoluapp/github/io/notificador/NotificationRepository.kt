package devoluapp.github.io.notificador

interface NotificationRepository {
    suspend fun geraTextoAfirmacao() : String
}