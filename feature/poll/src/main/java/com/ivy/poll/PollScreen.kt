package com.ivy.poll

import androidx.compose.runtime.Composable
import com.ivy.navigation.screenScopedViewModel

@Composable
fun PollScreen() {
  val viewModel: PollViewModel = screenScopedViewModel()

  PollUi(
    uiState = viewModel.uiState(),
    onEvent = viewModel::onEvent,
  )
}

@Composable
private fun PollUi(
  uiState: PollUiState,
  onEvent: (PollUiEvent) -> Unit
) {
  // TODO
}
