package com.ivy.releases

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
            ReleasesEvent.OnTryAgainClicked -> onTryAgainClicked()
        }
    }

    private suspend fun fetchReleaseInfo(): ReleasesState {
        val response = releasesDataSource.fetchReleaseInfo()

        if (response == null) {
            releaseState.value = ReleasesState.Error("Error")
            return releaseState.value
        }

        val releaseInfo = response.map {
            ReleaseInfo(
                releaseName = it.releaseName,
                releaseUrl = it.releaseUrl,
                releaseDate = releasesContentParser.toCustomDate(it.releaseDate),
                releaseCommits = releasesContentParser.toCustomList(it.commits)
            )
        }.toImmutableList()

        releaseState.value = ReleasesState.Success(releasesInfo = releaseInfo)
        return releaseState.value
    }

    private fun onTryAgainClicked(): ReleasesState {
        releaseState.value = ReleasesState.Loading(loadingMessage)

        viewModelScope.launch {
            fetchReleaseInfo()
        }

        return releaseState.value
    }
}