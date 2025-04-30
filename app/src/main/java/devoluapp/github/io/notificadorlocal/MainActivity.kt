package devoluapp.github.io.notificadorlocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import devoluapp.github.io.notificator.Notificator
import devoluapp.github.io.notificadorlocal.data.NotificationRepositoryImpl
import devoluapp.github.io.notificadorlocal.ui.screens.ConfigScreen
import devoluapp.github.io.notificadorlocal.ui.theme.NotificadorLocalTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permissão concedida
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = NotificationRepositoryImpl()
        // App fornece o repositório
        Notificator.setRepository(repository)

        // Apenas delega para a biblioteca
        Notificator.solicitarPermissaoDeNotificacao(this) { granted ->
            if (granted) {
                // (opcional) callback se quiser fazer algo quando for concedida
            }
        }

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