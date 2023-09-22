package com.ivy.releases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val releasesDataSource: ReleasesDataSource
) : ComposeViewModel<ReleasesState, ReleasesEvent>() {
    val releaseState = mutableStateOf<ReleasesState>(ReleasesState.Loading(""))

    @Composable
    override fun uiState(): ReleasesState {
        return releaseState.value
    }

    override fun onEvent(event: ReleasesEvent) {
        TODO("Not yet implemented")
    }

    private suspend fun fetchReleaseInfo(): List<ReleaseInfo>? {
        return releasesDataSource.fetchReleaseInfo()?.map {
            ReleaseInfo(
                releaseName = releaseName,

                )
        }
    }
}