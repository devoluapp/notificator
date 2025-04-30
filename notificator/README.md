
## O que faz?

Biblioteca, que encapsula a lógica de envio de notificações periódicas durante um intervalo (hora de início e hora de fim) durante o dia.

## Como Usar

1. Importar a biblioteca no `build.gradle.kts` do módulo

```kotlin 
implementation(project(":notificador-periodico"))
```

2. Criar, no projeto, uma classe que implemente a interface `NotificationRepository`. A função do único método dessa interface é fornecer o texto da notificação que será exibida. Exemplo didático abaixo:
```kotlin
import devoluapp.github.io.notificador.NotificationRepository

class NotificationRepositoryImpl : NotificationRepository{  
    override suspend fun getNotificationText(): String {  
        return "Esse será o texto da notificação"  
    }  
}
```

Na MainActivity de seu projeto, no onCreate instanciar o repositório concreto, configurá-lo no NotificadorUtils.

Opcionalmente, mas altamente recomendável, também chamar o método `solicitarPermissaoDeNotificacao`, pois ele encapsula o tratamento da ausência de permissões para que o aplicativo envie notificações, solicitando-as se o Android for o 13 ou maior (que exige essa permissão explícita).

```kotlin
val repository = NotificationRepositoryImpl()  
// App fornece o repositório  
NotificadorUtils.setRepository(repository)  
  
// Apenas delega para a biblioteca  
NotificadorUtils.solicitarPermissaoDeNotificacao(this) { granted ->  
    if (granted) {  
        // (opcional) callback se quiser fazer algo quando for concedida  
    }  
}
```

