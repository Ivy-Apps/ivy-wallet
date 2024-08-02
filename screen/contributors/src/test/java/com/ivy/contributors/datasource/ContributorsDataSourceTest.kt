package com.ivy.contributors.datasource

import arrow.core.left
import arrow.core.right
import org.junit.Test
import com.ivy.contributors.IvyWalletRepositoryDataSource.ContributorDto
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ContributorsDataSourceTest {

    private var dataSource = mockk<FakeDataSource>()

    @Test
    fun `Fetch contributors from request, happy path`() = runTest {
        val result = listOf(
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
        coEvery { dataSource.getContributorsFromRequest(PAGE_WITH_RESULT) } returns result.right()
        dataSource.getContributorsFromRequest(PAGE_WITH_RESULT) shouldBe result.right()
    }

    @Test
    fun `Fetch contributors from request, unhappy path`() = runTest {
        val errorMessage = "Error"
        coEvery { dataSource.getContributorsFromRequest(WRONG_QUERY) } returns errorMessage.left()
        dataSource.getContributorsFromRequest(WRONG_QUERY) shouldBe errorMessage.left()
    }

    @Test
    fun `Paging Source, happy path`() = runTest {
        val result = listOf(
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
        coEvery { dataSource.pagingSource() } returns result.right()
        dataSource.pagingSource() shouldBe result.right()
    }

    @Test
    fun `Paging Source, unhappy path`() = runTest {
        val errorMessage = "Error"
        coEvery { dataSource.pagingSource() } returns errorMessage.left()
        dataSource.pagingSource() shouldBe errorMessage.left()
    }

    @Test
    fun `Fetch Contributors, happy path`() = runTest {
        val result = listOf(
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
        coEvery { dataSource.fetchContributors() } returns result.right()
        dataSource.fetchContributors() shouldBe result.right()
    }

    @Test
    fun `Fetch Contributors, unhappy path`() = runTest {
        val errorMessage = "Error"
        coEvery { dataSource.fetchContributors() } returns errorMessage.left()
        dataSource.fetchContributors() shouldBe errorMessage.left()
    }

    companion object {
        private const val WRONG_QUERY = -1
        private const val PAGE_WITH_RESULT = 1
    }
}