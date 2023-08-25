package com.ivy.wallet.backup.github

import arrow.core.Either
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
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

    suspend fun commitFileToRepo(
        path: String,
        content: String,
    ): Either<String, Unit> {
        // https://github.com/Ivy-Apps/ivy-wallet
        val url = "" //"https://api.github.com/repos/$owner/$repo/contents/$path"
        val encodedContent = content.toByteArray(Charsets.UTF_8).encodeBase64()

        val requestBody = GitHubFileContent(
            content = encodedContent,
            message = "Committing JSON from Ktor",
            committer = Committer(name = "Your Name", email = "your.email@example.com")
        )

        httpClient.get().put(url) {
            headers {
//                append("Authorization", "token $token")
            }
            setBody(requestBody)
        }
        return Either.Right(Unit)
    }
}