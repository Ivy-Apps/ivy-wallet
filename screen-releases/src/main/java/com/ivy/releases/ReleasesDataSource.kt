package com.ivy.releases

import androidx.annotation.Keep
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ReleasesDataSource @Inject constructor(
    private val httpClient: HttpClient
) {

    @Keep
    @Serializable
    data class ReleaseDto(
        @SerialName("tag_name")
        val releaseName: String,
        @SerialName("html_url")
        val releaseUrl: String,
        @SerialName("published_at")
        val releaseDate: String,
        @SerialName("body")
        val commits: String?
    )

    suspend fun fetchReleaseInfo(): List<ReleaseDto>? {
        return try {
            withContext(Dispatchers.IO) {
                httpClient.get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/releases")
                    .body<List<ReleaseDto>?>()
            }
        } catch (e: Exception) {
            null
        }
    }
}