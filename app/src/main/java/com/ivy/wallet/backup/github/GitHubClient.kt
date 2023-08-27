package com.ivy.wallet.backup.github

import androidx.annotation.Keep
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
    private val httpClient: Lazy<HttpClient>,
) {
    @Keep
    @Serializable
    data class GitHubFileContent(
        val content: String,
        val message: String,
        val committer: Committer,
        val sha: String?,
    )

    @Keep
    @Serializable
    data class Committer(val name: String, val email: String)

    @Keep
    @Serializable
    data class GitHubFileResponse(val sha: String)

    suspend fun commit(
        credentials: GitHubCredentials,
        path: String,
        content: String,
        isAutomatic: Boolean = false,
    ): Either<String, Unit> = either {
        val url = repoUrl(credentials, path)
        val sha = getExistingFileSha(credentials, url)

        val encodedContent = content.toByteArray(Charsets.UTF_16).encodeBase64()

        val requestBody = GitHubFileContent(
            content = encodedContent,
            message = if (isAutomatic) {
                "Automatic Ivy Wallet data backup"
            } else {
                "Manual Ivy Wallet data backup"
            },
            committer = Committer(
                name = "Ivy Wallet",
                email = "automation@ivywallet.com"
            ),
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
            when (response.status.value) {
                404 -> "Invalid GitHub repo url."
                403 -> "Invalid GitHub PAT (Personal Access Token). Check your PAT permissions and expiration day."
                else -> "Unsuccessful response: '${response.status}' $response."
            }
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
        append("Authorization", "token ${credentials.gitHubPAT}")
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