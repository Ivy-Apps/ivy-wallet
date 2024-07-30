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
import timber.log.Timber
import javax.inject.Inject

class IvyWalletRepositoryDataSource @Inject constructor(
    private val httpClient: HttpClient
) {
    @Keep
    @Serializable
    data class ContributorDto(
        val login: String? = null,
        @SerialName("avatar_url")
        val avatarUrl: String? = null,
        val contributions: Int,
        @SerialName("html_url")
        val link: String? = null,
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
        private const val DISPLAY_ANONYMOUS_CONTRIBUTORS = true
        private const val INITIAL_PAGE = 1
    }

    suspend fun fetchContributors(): List<ContributorDto> {
        return withContext(Dispatchers.IO) {
            val contributors = mutableListOf<ContributorDto>()
            paging(addContributors = { results ->
                results?.forEach { contributor -> contributors.add(contributor) }
            })
            contributors.toList()
        }
    }

    private suspend fun paging(addContributors: (List<ContributorDto>?) -> Unit) {
        var currentPage: Int? = INITIAL_PAGE
        while (currentPage != null) {
            val contributors = try {
                httpClient
                    .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors") {
                        parameter("anon", DISPLAY_ANONYMOUS_CONTRIBUTORS)
                        parameter("per_page", CONTRIBUTORS_PER_PAGE)
                        parameter("page", currentPage)
                    }
                    .body<List<ContributorDto>>()
            } catch (e: Exception) {
                Timber.tag("FETCH_CONTRIBUTORS").d(e)
                null
            }
            currentPage = getCurrentPage(contributors, currentPage)
            currentPage?.let { addContributors(contributors) }
        }
    }

    private fun getCurrentPage(
        contributors: List<ContributorDto>?,
        currentPage: Int
    ): Int? {
        return if (contributors?.isEmpty() == true) {
            null
        } else {
            currentPage + 1
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
