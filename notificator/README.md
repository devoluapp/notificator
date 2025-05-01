

## O que faz?

Biblioteca, que encapsula a lógica de envio de notificações periódicas durante um intervalo (hora de início e hora de fim) durante o dia.
Permite:
- fornecer um repositório para os textos de notificações
- configurar horário de inicio e fim em que as notificações serão enviadas
## Como Usar

### Importanto a biblioteca no projeto

<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

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


2. Criar uma classe que implemente a interface `NotificationRepository` da biblioteca

3. Criar, no projeto, uma classe que implemente a interface `NotificationRepository`. A função do único método dessa interface é fornecer o texto da notificação que será exibida. Exemplo didático abaixo:
```kotlin  
import devoluapp.github.io.notificador.NotificationRepository  

class NotificationRepositoryImpl : NotificationRepository{    
	override suspend fun getNotificationText(): String {    
        return "Esse será o texto da notificação"    
	}  
}  
```  


Na MainActivity de seu projeto, no onCreate instanciar o repositório concreto e configurá-lo na biblioteca.

Chamar o método utilitário da biblioteca que verifica se o aplicativo já tem permissão de notificação (caso contrário, pede a permissão ao usuário).

```kotlin  
// Instancia o repositório criado, que implementa a interface NotificationRepository
val repository = NotificationRepositoryImpl()  

// Configura o repository na biblioteca (Importante! - sem isso ela não funciona!)
Notificator.setRepository(repository)  

// Verifica se o aplicativo já possui permissão de notificação fornecida pelo usuário. Caso contrário, pede a permissão. 
Notificator.solicitarPermissaoDeNotificacao(this)
```
