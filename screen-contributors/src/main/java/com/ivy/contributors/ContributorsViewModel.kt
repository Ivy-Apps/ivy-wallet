package com.ivy.contributors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.domain.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val ivyWalletRepositoryDataSource: IvyWalletRepositoryDataSource
) : ComposeViewModel<ContributorsState, ContributorsEvent>() {

    private val projectResponse = mutableStateOf<ProjectResponse>(ProjectResponse.Loading)
    private val contributorsResponse =
        mutableStateOf<ContributorsResponse>(ContributorsResponse.Loading)

    @Composable
    override fun uiState(): ContributorsState {
        LaunchedEffect(Unit) {
            fetchContributors()
            fetchProjectInfo()
        }

        return ContributorsState(
            projectResponse = projectResponse.value,
            contributorsResponse = contributorsResponse.value
        )
    }

    override fun onEvent(event: ContributorsEvent) {
        when (event) {
            ContributorsEvent.TryAgainButtonClicked -> onTryAgainButtonClicked()
        }
    }

    private fun onTryAgainButtonClicked() {
        contributorsResponse.value = ContributorsResponse.Loading

        viewModelScope.launch {
            fetchContributors()
        }
    }

    private suspend fun fetchContributors() {
        val contributors = ivyWalletRepositoryDataSource.fetchContributors()?.map {
            Contributor(
                name = it.login,
                photoUrl = it.avatarUrl,
                contributionsCount = it.contributions.toString(),
                githubProfileUrl = it.link
            )
        }

        if (contributors != null) {
            contributorsResponse.value = ContributorsResponse.Success(
                contributors.toImmutableList()
            )
        } else {
            contributorsResponse.value = ContributorsResponse.Error("Error")
        }
    }

    private suspend fun fetchProjectInfo() {
        val responseInfo = ivyWalletRepositoryDataSource.fetchRepositoryInfo()

        if (responseInfo != null) {
            val projectRepositoryInfo = ProjectRepositoryInfo(
                forks = responseInfo.forks.toString(),
                stars = responseInfo.stars.toString(),
                url = responseInfo.url
            )

            projectResponse.value = ProjectResponse.Success(projectRepositoryInfo)
        } else {
            projectResponse.value = ProjectResponse.Error
        }
    }
}