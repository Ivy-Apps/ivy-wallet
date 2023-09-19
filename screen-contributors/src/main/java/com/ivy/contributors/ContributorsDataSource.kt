package com.ivy.contributors

import androidx.annotation.Keep
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ContributorsDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    @Keep
    @Serializable
    data class ContributorDto(
        val login: String,
        @SerialName("avatar_url")
        val avatarUrl: String,
        val contributions: Int,
        @SerialName("html_url")
        val link: String
    )

    suspend fun fetchContributors(): List<ContributorDto>? {
        return try {
            withContext(Dispatchers.IO) {
                httpClient
                    .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors")
                    .body<List<ContributorDto>>()
            }
        } catch (e: Exception) {
            null
        }
    }
}