package com.ivy.wallet.backup.github

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
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
    data class GitHubFileContent(
        val content: String,
        val message: String,
        val committer: Committer,
        val sha: String?,
    )

    @Serializable
    data class Committer(val name: String, val email: String)

    @Serializable
    data class GitHubFileResponse(val sha: String)

    suspend fun commit(
        path: String,
        content: String,
    ): Either<String, Unit> = either {
        val credentials = credentialsManager.getCredentials().bind()

        val url = repoUrl(credentials, path)
        val sha = getExistingFileSha(credentials, url)

        val encodedContent = content.toByteArray(Charsets.UTF_16).encodeBase64()

        val requestBody = GitHubFileContent(
            content = encodedContent,
            message = "Committing from Ktor",
            committer = Committer(name = "Ivy Wallet", email = "ivywalelt@ivy-bot.com"),
            sha = sha,
        )

        val response = httpClient.get().put(url) {
            headers {
                githubToken(credentials)
                contentType(ContentType.Application.Json)
                acceptsUtf16()
            }
            setBody(requestBody)
        }
        ensure(response.status.isSuccess()) {
            "Unsuccessful response: ${response.status}"
        }
        return Either.Right(Unit)
    }

    private suspend fun getExistingFileSha(
        credentials: GitHubCredentials,
        url: String
    ): String? = catch({
        // Fetch the current file to get its SHA
        httpClient.get().get(url) {
            headers {
                githubToken(credentials)
            }
        }.body<GitHubFileResponse>().sha

    }) {
        null
    }

    private fun HeadersBuilder.githubToken(credentials: GitHubCredentials) {
        append("Authorization", "token ${credentials.accessToken}")
    }

    private fun HeadersBuilder.acceptsUtf16() {
        append("Accept-Charset", "UTF-16")
    }

    private fun repoUrl(
        credentials: GitHubCredentials,
        path: String
    ): String {
        return "https://api.github.com/repos/${credentials.owner}/${credentials.repo}/contents/$path"
    }
}