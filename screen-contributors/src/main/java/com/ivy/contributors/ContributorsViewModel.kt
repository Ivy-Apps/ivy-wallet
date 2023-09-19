package com.ivy.contributors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val contributorsDataSource: ContributorsDataSource
) :
    ComposeViewModel<ContributorsState, ContributorsEvent>() {
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
        }

        return ContributorsState(contributors = contributors.value?.toImmutableList())
    }

    override fun onEvent(event: ContributorsEvent) {
        TODO("Not yet implemented")
    }
}