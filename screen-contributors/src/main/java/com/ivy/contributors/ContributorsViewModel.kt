package com.ivy.contributors

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val contributionsResponse: ContributionsResponse
) :
    ComposeViewModel<ContributorsState, ContributorsEvent>() {
    @Composable
    override fun uiState(): ContributorsState {
        return ContributorsState(
            contributors = persistentListOf(
                Contributor(
                    name = "",
                    photo = "",
                    contributions = "",
                    link = ""
                )
            )
        )
    }

    override fun onEvent(event: ContributorsEvent) {
        TODO("Not yet implemented")
    }
}