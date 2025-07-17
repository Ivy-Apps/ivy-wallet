package com.ivy.poll.impl.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.poll.impl.ui.PollUiEvent
import com.ivy.poll.impl.ui.PollUiState

@Composable
fun PollContent(
  uiState: PollUiState.Content,
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
      text = uiState.poll.description,
      style = MaterialTheme.typography.bodyLarge,
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
      loading = uiState.voteLoading,
      onClick = {
        onEvent(PollUiEvent.VoteClick)
      }
    )
    Spacer(Modifier.height(24.dp))
  }
}

@Composable
private fun VoteButton(
  enabled: Boolean,
  loading: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Button(
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp),
    enabled = enabled && !loading,
    onClick = onClick,
  ) {
    if (loading) {
      CircularProgressIndicator(
        modifier = Modifier.size(24.dp)
      )
    } else {
      Text(text = "Vote")
    }
  }
}