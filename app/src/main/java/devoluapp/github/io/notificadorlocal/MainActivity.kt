package devoluapp.github.io.notificadorlocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import devoluapp.github.io.notificadorlocal.data.NotificationRepositoryImpl
import devoluapp.github.io.notificadorlocal.ui.screens.ConfigScreen
import devoluapp.github.io.notificadorlocal.ui.theme.NotificadorLocalTheme
import devoluapp.github.io.notificator.Notificator

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = NotificationRepositoryImpl()

        Notificator.setRepository(repository)

        Notificator.solicitarPermissaoDeNotificacao(this)

        setContent {
            NotificadorLocalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConfigScreen()
                }
            }
        }
    }
}

