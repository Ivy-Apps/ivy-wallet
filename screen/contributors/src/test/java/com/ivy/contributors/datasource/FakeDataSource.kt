package com.ivy.contributors.datasource

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.base.threading.DispatchersProvider
import com.ivy.contributors.IvyWalletRepositoryDataSource.ContributorDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.withContext

/**
 * @implSpec
 * The purpose of this class is to test methods that cannot be public in IvyWalletRepositoryDataSource class.
 * @see com.ivy.contributors.IvyWalletRepositoryDataSource
 */
class FakeDataSource(private val httpClient: HttpClient, private val dispatchersProvider: DispatchersProvider) {

    companion object {
        private const val INITIAL_PAGE = 1
    }

    suspend fun fetchContributors(): Either<String, List<ContributorDto>> = either {
        withContext(dispatchersProvider.io) {
            pagingSource().bind()
        }
    }

    suspend fun pagingSource(): Either<String, List<ContributorDto>> = either {
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

    suspend fun getContributorsFromRequest(currentPage: Int): Either<String, List<ContributorDto>> =
        either {
            arrow.core.raise.catch({
                httpClient
                    .get("https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors") {
                        parameter("page", currentPage)
                    }
                    .body<List<ContributorDto>>()
            }) { e ->
                raise(e.message ?: "Unknown Error")
            }
        }

    fun getNextPage(
        contributors: List<ContributorDto>?,
        currentPage: Int?,
    ): Int? = if (contributors?.isEmpty() == true) null else currentPage?.plus(1)
}