package com.ivy.releases

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
class ReleasesViewModel @Inject constructor(
    private val releasesDataSource: ReleasesDataSource,
    private val releasesContentParser: ReleasesContentParser
) : ComposeViewModel<ReleasesState, ReleasesEvent>() {
    private val loadingMessage = "Loading..."
    private val releaseState = mutableStateOf<ReleasesState>(
        ReleasesState.Loading(loadingMessage)
    )

    @Composable
    override fun uiState(): ReleasesState {
        LaunchedEffect(Unit) {
            fetchReleaseInfo()
        }

        return releaseState.value
    }

    override fun onEvent(event: ReleasesEvent) {
        when (event) {
            ReleasesEvent.OnTryAgainClick -> onTryAgainClick()
        }
    }

    private fun onTryAgainClick() {
        releaseState.value = ReleasesState.Loading(loadingMessage)

        viewModelScope.launch {
            fetchReleaseInfo()
        }
    }

    private suspend fun fetchReleaseInfo() {
        val response = releasesDataSource.fetchReleaseInfo()

        if (response == null) {
            releaseState.value = ReleasesState.Error("Error")
            return
        }

        val releaseInfo = response.map {
            ReleaseInfo(
                releaseName = it.releaseName,
                releaseUrl = it.releaseUrl,
                releaseDate = releasesContentParser.toReleaseDate(it.releaseDate),
                releaseCommits = releasesContentParser.toCommitsList(it.commits)
            )
        }.toImmutableList()

        releaseState.value = ReleasesState.Success(releasesInfo = releaseInfo)
    }
}