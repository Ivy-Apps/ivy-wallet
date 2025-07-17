package com.ivy.poll.impl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.navigation.screenScopedViewModel
import com.ivy.poll.impl.ui.composables.VoteCard

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
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
  ) {
    Spacer(Modifier.height(48.dp))
    Text(
      text = "To continue to exist, Ivy Wallet needs maintenance." +
          " Updating it requires effort and we can't do it for free",
      style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(48.dp))
    VoteCard(
      poll = uiState.poll,
      selectedIndex = uiState.selectedIndex,
      onOptionClick = {
        onEvent(PollUiEvent.SelectOption(index = it))
      }
    )
    Spacer(Modifier.height(24.dp))
    VoteButton(
      enabled = uiState.voteEnabled,
      onClick = {
        onEvent(PollUiEvent.VoteClick)
      }
    )
    Spacer(Modifier.height(24.dp))
  }
}

@Composable
fun VoteButton(
  enabled: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier.fillMaxWidth(),
    enabled = enabled,
    onClick = onClick,
  ) {
    Text(text = "Vote")
  }
}
