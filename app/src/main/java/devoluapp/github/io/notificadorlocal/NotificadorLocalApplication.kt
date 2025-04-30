package devoluapp.github.io.notificadorlocal

import android.app.Application
import devoluapp.github.io.notificador.NotificadorUtils
import devoluapp.github.io.notificadorlocal.data.NotificationRepositoryImpl

class NotificadorLocalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val repository = NotificationRepositoryImpl()
        // App fornece o repositório
        NotificadorUtils.setRepository(repository)
    }
}