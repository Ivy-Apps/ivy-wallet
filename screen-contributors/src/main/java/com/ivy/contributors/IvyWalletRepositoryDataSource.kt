package com.ivy.contributors

import androidx.annotation.Keep
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class IvyWalletRepositoryDataSource @Inject constructor(
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

    @Keep
    @Serializable
    data class IvyWalletRepositoryInfo(
        @SerialName("forks")
        val forks: Int,
        @SerialName("stargazers_count")
        val stars: Int,
        @SerialName("html_url")
        val url: String
    )

    companion object {
        private const val CONTRIBUTORS_PER_PAGE = 100
    }

    suspend fun fetchContributors(): List<ContributorDto>? {
        return try {
            withContext(Dispatchers.IO) {
                httpClient
                    .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors") {
                        parameter("per_page", CONTRIBUTORS_PER_PAGE)
                    }
                    .body<List<ContributorDto>>()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchRepositoryInfo(): IvyWalletRepositoryInfo? {
        return try {
            withContext(Dispatchers.IO) {
                httpClient
                    .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet")
                    .body<IvyWalletRepositoryInfo>()
            }
        } catch (e: Exception) {
            null
        }
    }
}