package com.ivy.poll.impl.ui

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PollUiState(
  val poll: PollUi,
  val selectedIndex: Int?,
  val voteEnabled: Boolean,
)

data class PollUi(
  val title: String,
  val options: ImmutableList<String>
)

sealed interface PollUiEvent {
  data class SelectOption(val index: Int) : PollUiEvent
  data object VoteClick : PollUiEvent
}