package com.ivy.contributors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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

    private val contributors = mutableStateOf<List<Contributor>?>(null)
    private val contributorsStage = mutableStateOf<ContributorsStage>(ContributorsStage.Loading)

    @Composable
    override fun uiState(): ContributorsState {
        LaunchedEffect(Unit) {
            contributors.value = contributorsDataSource.fetchContributors()?.map {
                Contributor(
                    name = it.login,
                    photo = it.avatarUrl,
                    contributions = it.contributions.toString(),
                    link = it.link
                )
            }

            val contributors = contributors.value

            if (contributors != null) {
                contributorsStage.value = ContributorsStage.Success(contributors.toImmutableList())
            } else {
                contributorsStage.value = ContributorsStage.Error("Error. Try again.")
            }
        }

        return ContributorsState(contributorsStage.value)
    }

    override fun onEvent(event: ContributorsEvent) {
        when (event) {
            ContributorsEvent.TryAgainButtonClicked -> onTryAgainButtonClicked()
        }
    }

    private fun onTryAgainButtonClicked() {
        viewModelScope.launch {
            contributors.value = contributorsDataSource.fetchContributors()?.map {
                Contributor(
                    name = it.login,
                    photo = it.avatarUrl,
                    contributions = it.contributions.toString(),
                    link = it.link
                )
            }
        }
    }
}