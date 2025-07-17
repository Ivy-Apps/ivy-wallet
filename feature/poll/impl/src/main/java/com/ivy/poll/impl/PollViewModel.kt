package com.ivy.poll.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

@Stable
@HiltViewModel
class PollViewModel @Inject constructor() : ComposeViewModel<PollUiState, PollUiEvent>() {
  private var options by mutableStateOf(persistentListOf<Option>())

  @Composable
  override fun uiState(): PollUiState {
    return PollUiState(
      options = options
    )
  }

  override fun onEvent(event: PollUiEvent) {
    // TODO
  }
}