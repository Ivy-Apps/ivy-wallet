package com.ivy.releases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val releasesDataSource: ReleasesDataSource,
    private val releasesContentParser: ReleasesContentParser
) : ComposeViewModel<ReleasesState, ReleasesEvent>() {
    val releaseState = mutableStateOf<ReleasesState>(
        ReleasesState.Loading("Loading...")
    )

    @Composable
    override fun uiState(): ReleasesState {
        LaunchedEffect(Unit) {
            fetchReleaseInfo()
        }

        return releaseState.value
    }

    override fun onEvent(event: ReleasesEvent) {
        TODO("Not yet implemented")
    }

    private suspend fun fetchReleaseInfo(): ReleasesState {
        val response = releasesDataSource.fetchReleaseInfo() ?: return ReleasesState.Error(
            "Error"
        )

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
}