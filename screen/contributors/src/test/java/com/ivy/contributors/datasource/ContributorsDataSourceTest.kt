package com.ivy.contributors.datasource

import arrow.core.Either
import com.ivy.base.TestDispatchersProvider
import io.ktor.client.HttpClient
import org.junit.Before
import org.junit.Test
import com.ivy.contributors.IvyWalletRepositoryDataSource.ContributorDto
import com.ivy.contributors.ktor.IvyMockEngine
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.test.runTest

class ContributorsDataSourceTest {

    private val mockEngine = IvyMockEngine()
    private val httpClient = HttpClient(mockEngine.get()) {
        install(ContentNegotiation) {
            json()
        }
    }
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setup() {
        dataSource = spyk(FakeDataSource(httpClient, TestDispatchersProvider))
    }

    @Test
    fun `Fetch contributors from request, happy path`() = runTest {
        val expected = Either.Right(
            listOf(
                ContributorDto(
                    login = "a_login",
                    avatarUrl = "a_avatar",
                    contributions = 685,
                    link = "a_link"
                ),
                ContributorDto(
                    login = "b_login",
                    avatarUrl = "b_avatar",
                    contributions = 101,
                    link = "b_link"
                ),
            )
        )
        dataSource.getContributorsFromRequest(PAGE_WITH_RESULT) shouldBe expected
    }

    @Test
    fun `Fetch contributors from request, unhappy path`() = runTest {
        val expected = Either.Left("Error")
        dataSource.getContributorsFromRequest(WRONG_QUERY) shouldBe expected
    }

    @Test
    fun `Paging Source, happy path`() = runTest {
        val expected = Either.Right(
            listOf(
                ContributorDto(
                    login = "a_login",
                    avatarUrl = "a_avatar",
                    contributions = 685,
                    link = "a_link"
                ),
                ContributorDto(
                    login = "b_login",
                    avatarUrl = "b_avatar",
                    contributions = 101,
                    link = "b_link"
                ),
            )
        )
        coEvery { dataSource.pagingSource() } returns expected
        val result = dataSource.pagingSource()
        result shouldBe expected
    }

    @Test
    fun `Paging Source, unhappy path`() = runTest {
        coEvery { dataSource.pagingSource() } returns Either.Left("Error")
        val result = dataSource.pagingSource()
        result shouldBe Either.Left("Error")
    }

    companion object {
        private const val WRONG_QUERY = -1
        private const val PAGE_WITH_RESULT = 1
    }
}