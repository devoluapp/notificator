package devoluapp.github.io.notificadorlocal.data

import devoluapp.github.io.notificator.NotificationRepository

class NotificationRepositoryImpl : NotificationRepository{
    override suspend fun getNotificationText(): String {
        return obterAfirmacaoPositiva()
    }
}

fun obterAfirmacaoPositiva(): String {
    val afirmacoes = listOf(
        "Eu sou suficiente exatamente como sou.",
        "Mereço coisas boas e as recebo com gratidão.",
        "Confio em mim e na minha capacidade de superar desafios.",
        "Cada dia é uma nova oportunidade para crescer e evoluir.",
        "Eu me amo, me aceito e me respeito profundamente.",
        "Sou forte, resiliente e capaz de realizar meus sonhos.",
        "A vida me apoia em tudo o que escolho fazer.",
        "Eu irradio confiança, coragem e alegria.",
        "Tudo o que preciso está dentro de mim.",
        "Estou em paz com o passado e aberto(a) ao futuro brilhante que me espera."
    )
    return afirmacoes.random()
}