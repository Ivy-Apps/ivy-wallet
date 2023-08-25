package com.ivy.wallet.backup.github

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.encodeBase64
import kotlinx.serialization.Serializable
import javax.inject.Inject


class GitHubClient @Inject constructor(
    private val credentialsManager: GitHubCredentialsManager,
    private val httpClient: Lazy<HttpClient>,
) {
    @Serializable
    data class GitHubFileContent(val content: String, val message: String, val committer: Committer)

    @Serializable
    data class Committer(val name: String, val email: String)

    suspend fun commit(
        path: String,
        content: String,
    ): Either<String, Unit> = either {
        val credentials = credentialsManager.getCredentials().bind()

        val url =
            "https://api.github.com/repos/${credentials.owner}/${credentials.repo}/contents/$path"
        val encodedContent = content.toByteArray(Charsets.UTF_16).encodeBase64()

        val requestBody = GitHubFileContent(
            content = encodedContent,
            message = "Committing from Ktor",
            committer = Committer(name = "Ivy Wallet", email = "ivywalelt@ivy-bot.com")
        )

        val response = httpClient.get().put(url) {
            headers {
                append("Authorization", "token ${credentials.accessToken}")
                contentType(ContentType.Application.Json)
            }
            setBody(requestBody)
        }
        ensure(response.status.isSuccess()) {
            "Unsuccessful response: ${response.status}"
        }
        return Either.Right(Unit)
    }
}