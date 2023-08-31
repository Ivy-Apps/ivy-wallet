package com.ivy.wallet.backup.github

import androidx.annotation.Keep
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
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
    data class GitHubFileResponse(
        val sha: String,
        @SerialName("download_url")
        val downloadUrl: String,
    )

    suspend fun commit(
        credentials: GitHubCredentials,
        path: String,
        content: String,
        commitMsg: String,
    ): Either<String, Unit> = either {
        val repoUrl = repoUrl(credentials, path)
        val sha = getExistingFile(credentials, repoUrl)?.sha

        val encodedContent = content.toByteArray(Charsets.UTF_16).encodeBase64()

        val requestBody = GitHubFileContent(
            content = encodedContent,
            message = commitMsg,
            committer = Committer(
                name = "Ivy Wallet",
                email = "automation@ivywallet.com"
            ),
            sha = sha,
        )

        val response = try {
            httpClient.get().put(repoUrl) {
                headers {
                    githubToken(credentials)
                    contentType(ContentType.Application.Json)
                    acceptsUtf16()
                }
                setBody(requestBody)
            }
        } catch (e: Exception) {
            return Either.Left("HttpException: ${e.message}")
        }
        ensure(response.status.isSuccess()) {
            when (response.status.value) {
                404 -> "Invalid GitHub repo url."
                403 -> "Invalid GitHub PAT (Personal Access Token). Check your PAT permissions and expiration day."
                else -> "Unsuccessful response: '${response.status}' $response."
            }
        }
    }

    suspend fun readFileContent(
        credentials: GitHubCredentials,
        path: String,
    ): Either<String, String> = either {
        val repoUrl = repoUrl(credentials, path)
        val file = getExistingFile(credentials, repoUrl)
        ensureNotNull(file) {
            "Failed to fetch GitHub file '$repoUrl'."
        }
        val fileContent = downloadFileContent(credentials, file.downloadUrl).bind()
        ensure(fileContent.isNotBlank()) {
            "GitHub file content is blank!"
        }
        fileContent
    }

    private suspend fun downloadFileContent(
        credentials: GitHubCredentials,
        downloadUrl: String
    ): Either<String, String> = catch({
        val client = httpClient.get()
        val response = client.get(downloadUrl) {
            headers {
                githubToken(credentials)
            }
        }
        if (!response.status.isSuccess()) {
            error("Failed to download file with ${response.status} $response")
        }
        val byteArray = response.body<ByteArray>()

        val content = byteArray.toString(Charsets.UTF_16)
        Either.Right(content)
    }) {
        Timber.e("GitHub file download: $it")
        Either.Left("Failed to GitHub backup file because $it.")
    }

    private suspend fun getExistingFile(
        credentials: GitHubCredentials,
        url: String
    ): GitHubFileResponse? = catch({
        // Fetch the current file to get its SHA
        httpClient.get().get(url) {
            headers {
                githubToken(credentials)
            }
        }.body<GitHubFileResponse>()

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