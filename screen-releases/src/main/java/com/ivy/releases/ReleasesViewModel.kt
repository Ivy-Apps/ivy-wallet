package com.ivy.releases

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor() : ComposeViewModel<ReleasesState, ReleasesEvent>() {
    @Composable
    override fun uiState(): ReleasesState {
        TODO("Not yet implemented")
    }

    override fun onEvent(event: ReleasesEvent) {
        TODO("Not yet implemented")
    }
}