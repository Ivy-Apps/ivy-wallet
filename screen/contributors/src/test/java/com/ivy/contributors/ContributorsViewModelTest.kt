package com.ivy.contributors

import com.ivy.contributors.IvyWalletRepositoryDataSource.ContributorDto
import com.ivy.contributors.IvyWalletRepositoryDataSource.IvyWalletRepositoryInfo
import com.ivy.testing.ComposeViewModelTest
import com.ivy.testing.runTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Test

class ContributorsViewModelTest : ComposeViewModelTest() {

    private val repoDataSource = mockk<IvyWalletRepositoryDataSource>()

    private lateinit var viewModel: ContributorsViewModel

    @Before
    fun setup() {
        viewModel = ContributorsViewModel(repoDataSource)
    }

    @Test
    fun `happy path, both success`() {
        // given
        coEvery { repoDataSource.fetchContributors() } returns listOf(
            ContributorDto(
                login = "a",
                avatarUrl = "a_avatar",
                contributions = 7,
                link = "a_link"
            ),
            ContributorDto(
                login = "b",
                avatarUrl = "b_avatar",
                contributions = 42,
                link = "b_link"
            ),
        )
        coEvery { repoDataSource.fetchRepositoryInfo() } returns IvyWalletRepositoryInfo(
            forks = 300,
            stars = 2_000,
            url = "abc"
        )

        // then
        viewModel.runTest {
            contributorsResponse shouldBe ContributorsResponse.Success(
                persistentListOf(
                    Contributor(
                        name = "a",
                        photoUrl = "a_avatar",
                        contributionsCount = "7",
                        githubProfileUrl = "a_link"
                    ),
                    Contributor(
                        name = "b",
                        photoUrl = "b_avatar",
                        contributionsCount = "42",
                        githubProfileUrl = "b_link"
                    ),
                )
            )

            projectResponse shouldBe ProjectResponse.Success(
                ProjectRepositoryInfo(
                    forks = "300",
                    stars = "2000",
                    url = "abc"
                )
            )
        }
    }

    @Test
    fun `unhappy path, both error`() {
        // given
        coEvery { repoDataSource.fetchContributors() } returns null
        coEvery { repoDataSource.fetchRepositoryInfo() } returns null

        viewModel.runTest {
            contributorsResponse.shouldBeInstanceOf<ContributorsResponse.Error>()
            projectResponse.shouldBeInstanceOf<ProjectResponse.Error>()
        }
    }
}
