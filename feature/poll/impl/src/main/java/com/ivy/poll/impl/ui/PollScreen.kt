package com.ivy.poll.impl.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivy.navigation.screenScopedViewModel
import com.ivy.poll.impl.ui.composables.PollContent
import com.ivy.poll.impl.ui.composables.VotedContent

@Composable
fun PollScreen() {
  val viewModel: PollViewModel = screenScopedViewModel()

  PollUi(
    uiState = viewModel.uiState(),
    onEvent = viewModel::onEvent,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PollUi(
  uiState: PollUiState,
  onEvent: (PollUiEvent) -> Unit
) {
  Scaffold(
    modifier = Modifier.systemBarsPadding(),
    topBar = {
      TopAppBar(
        title = { Text("You decide") }
      )
    },
    content = { padding ->
      Content(
        modifier = Modifier.padding(padding),
        uiState = uiState,
        onEvent = onEvent,
      )
    }
  )
}

@Composable
private fun Content(
  uiState: PollUiState,
  onEvent: (PollUiEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  when (uiState) {
    is PollUiState.Content -> PollContent(
      modifier = modifier,
      uiState = uiState,
      onEvent = onEvent,
    )

    PollUiState.Voted -> VotedContent(
      onBackClick = {
        onEvent(PollUiEvent.BackClick)
      }
    )
  }
}
