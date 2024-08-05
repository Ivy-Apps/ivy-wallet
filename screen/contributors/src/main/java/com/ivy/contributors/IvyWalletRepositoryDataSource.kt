package com.ivy.contributors

import androidx.annotation.Keep
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import com.ivy.base.threading.DispatchersProvider
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
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
) {
    @Keep
    @Serializable
    @Suppress("DataClassDefaultValues")
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
        val url: String,
    )

    companion object {
        private const val CONTRIBUTORS_PER_PAGE = 100
        private const val DISPLAY_ANONYMOUS_CONTRIBUTORS = true
        private const val INITIAL_PAGE = 1
    }

    suspend fun fetchContributors(): Either<String, List<ContributorDto>> =
        withContext(dispatchersProvider.io) {
            pagingSource()
        }

    private suspend fun pagingSource(): Either<String, List<ContributorDto>> = either {
        val contributorsSource = mutableListOf<ContributorDto>()
        var currentPage: Int? = INITIAL_PAGE
        while (currentPage != null) {
            val contributorsResult = getContributorsFromRequest(currentPage)
            contributorsResult.onLeft { errorMessage ->
                currentPage = null
                raise(errorMessage)
            }
            contributorsResult.onRight { results ->
                currentPage = getNextPage(results, currentPage)
                currentPage?.let { contributorsSource.addAll(results) }
            }
            if (currentPage == null) { break }
        }
        contributorsSource.toList()
    }

    private suspend fun getContributorsFromRequest(currentPage: Int): Either<String, List<ContributorDto>> =
        catch({
            val contributorsDto = httpClient
                .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors") {
                    parameter("anon", DISPLAY_ANONYMOUS_CONTRIBUTORS)
                    parameter("per_page", CONTRIBUTORS_PER_PAGE)
                    parameter("page", currentPage)
                }
                .body<List<ContributorDto>>()
            Either.Right(contributorsDto)
        }) { e ->
            Either.Left(e.message ?: "Unknown Error")
        }

    private fun getNextPage(
        contributors: List<ContributorDto>?,
        currentPage: Int?,
    ): Int? = if (contributors?.isEmpty() == true) null else currentPage?.plus(1)

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
