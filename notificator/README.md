## O que faz?

Biblioteca, que encapsula a lógica de envio de notificações periódicas durante um intervalo (hora de início e hora de fim) durante o dia.
Permite:
- fornecer um repositório para os textos de notificações
- configurar horário de inicio e fim em que as notificações serão enviadas
- configurar o intervalo entre as notificações (mínimo de 15 minutos)

## Como Usar

### Importando a biblioteca no projeto

```xml
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

1. No settings.gradle.kts (nível do projeto), na seção `dependencyResolutionManagement` adicionar o repositório do jitpack:

```kotlin
dependencyResolutionManagement {  
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  
    repositories {  
        google()  
        mavenCentral()  
        maven { url = uri("https://jitpack.io") }
    }  
}
```

No `build.gradle.kts` (nível do módulo) adicione a dependência
```kotlin
dependencies {
    // ... outras dependências
    implementation("com.github.devoluapp.notificador:0.0.1")
}
```

### Usando a biblioteca

1. Declare a necessidade de permissão para notificações no Android Manifest:

```xml
<?xml version="1.0" encoding="utf-8"?>  
<manifest xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools">  
  
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <!--> ... </!-->
</manifest>
```

2. Criar uma classe que implemente a interface `NotificationRepository` da biblioteca. A implementação deve incluir um BroadcastReceiver para responder às solicitações de texto quando o aplicativo não estiver em execução:

```kotlin
class NotificationRepositoryImpl : NotificationRepository {
    private var currentText: String = "Texto padrão da notificação"
    
    override suspend fun getNotificationText(): String {
        return currentText
    }
    
    override suspend fun updateNotificationText(text: String) {
        currentText = text
    }
}

// BroadcastReceiver para responder às solicitações de texto
class NotificationTextBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "devoluapp.github.io.notificator.ACTION_GET_NOTIFICATION_TEXT") {
            // Obtém o texto atual do repositório
            val repository = NotificationRepositoryImpl()
            val text = repository.getNotificationText()
            
            // Envia o texto de volta via broadcast
            val responseIntent = Intent("devoluapp.github.io.notificator.ACTION_GET_NOTIFICATION_TEXT")
            responseIntent.putExtra("notification_text", text)
            context.sendBroadcast(responseIntent)
        }
    }
}
```

3. Registrar o BroadcastReceiver no AndroidManifest.xml:

```xml
<manifest>
    <application>
        <!-- ... outras configurações ... -->
        
        <receiver
            android:name=".NotificationTextBroadcastReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="devoluapp.github.io.notificator.ACTION_GET_NOTIFICATION_TEXT" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

4. Na MainActivity de seu projeto, no onCreate instanciar o repositório concreto e configurá-lo na biblioteca:

```kotlin
// Instancia o repositório criado, que implementa a interface NotificationRepository
val repository = NotificationRepositoryImpl()

// Configura o repository na biblioteca (Importante! - sem isso ela não funciona!)
Notificator.setRepository(repository)

// Verifica se o aplicativo já possui permissão de notificação fornecida pelo usuário
Notificator.solicitarPermissaoDeNotificacao(this)
```

### Configurando o intervalo entre notificações

O intervalo entre notificações pode ser configurado através do método `saveInterval`. O intervalo deve seguir as seguintes regras:

1. Não pode ser menor que 15 minutos
2. Não pode ser maior que o tempo total entre o horário de início e fim das notificações

Exemplo de uso:

```kotlin
// Configura o intervalo para 30 minutos
notificator.saveInterval(30)

// Agenda as notificações com o intervalo configurado
notificator.scheduleNotifications("08:00", "20:00", 30, R.drawable.ic_notification)
```

Se tentar configurar um intervalo inválido, uma `IllegalArgumentException` será lançada com uma mensagem explicativa.

### Notas Importantes

1. O BroadcastReceiver é essencial para que as notificações funcionem mesmo quando o aplicativo não está em execução
2. O texto padrão será usado se o BroadcastReceiver não responder em 5 segundos
3. O repositório deve manter o estado do texto atual para garantir consistência
4. O BroadcastReceiver deve ser registrado no AndroidManifest.xml com `android:exported="true"` para permitir que a biblioteca o acesse
