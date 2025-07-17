package com.ivy.poll.impl.ui

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface PollUiState {
  data class Content(
    val poll: PollUi,
    val selectedIndex: Int?,
    val voteEnabled: Boolean,
    val voteLoading: Boolean,
  ) : PollUiState

  data object Voted : PollUiState
}

data class PollUi(
  val title: String,
  val description: String,
  val options: ImmutableList<String>
)

sealed interface PollUiEvent {
  data object BackClick : PollUiEvent
  data class SelectOption(val index: Int) : PollUiEvent
  data object VoteClick : PollUiEvent
}