package ivy.automate.base.ktor

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface KtorClientScope {
    val ktorClient: HttpClient
}

fun newKtorClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }
}

suspend fun ktorClientScope(block: suspend KtorClientScope.() -> Unit) {
    val ktorClient by lazy { newKtorClient() }
    val scope = object : KtorClientScope {
        override val ktorClient: HttpClient = ktorClient
    }
    ktorClient.use { scope.block() }
}