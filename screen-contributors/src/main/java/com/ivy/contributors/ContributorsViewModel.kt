package com.ivy.contributors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val contributorsDataSource: ContributorsDataSource
) : ComposeViewModel<ContributorsState, ContributorsEvent>() {

    private val contributorsState = mutableStateOf<ContributorsState>(ContributorsState.Loading)

    @Composable
    override fun uiState(): ContributorsState {
        val contributors = remember { mutableStateOf<List<Contributor>?>(null) }

        LaunchedEffect(Unit) {
            contributors.value = contributorsDataSource.fetchContributors()?.map {
                Contributor(
                    name = it.login,
                    photo = it.avatarUrl,
                    contributions = it.contributions.toString(),
                    link = it.link
                )
            }

            val contributorsList = contributors.value

            if (contributorsList != null) {
                contributorsState.value = ContributorsState.Success(
                    contributorsList.toImmutableList()
                )
            } else {
                contributorsState.value = ContributorsState.Error("Error. Try again.")
            }
        }

        return contributorsState.value
    }

    override fun onEvent(event: ContributorsEvent) {
        when (event) {
            ContributorsEvent.TryAgainButtonClicked -> onTryAgainButtonClicked()
        }
    }

    private fun onTryAgainButtonClicked() {
        contributorsState.value = ContributorsState.Loading

        viewModelScope.launch {
            val contributors = contributorsDataSource.fetchContributors()?.map {
                Contributor(
                    name = it.login,
                    photo = it.avatarUrl,
                    contributions = it.contributions.toString(),
                    link = it.link
                )
            }

            if (contributors != null) {
                contributorsState.value = ContributorsState.Success(
                    contributors.toImmutableList()
                )
            } else {
                contributorsState.value = ContributorsState.Error("Error. Try again.")
            }
        }
    }
}