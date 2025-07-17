package com.ivy.poll.impl

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class PollUiState(
  val options: ImmutableList<Option>,
)

@JvmInline
value class Option(val name: String)

sealed interface PollUiEvent {
  data class SelectOption(val option: Option) : PollUiEvent
  data object VoteClick : PollUiEvent
  data object ConfirmVoteClick : PollUiEvent
  data object CancelVoteClick : PollUiEvent
}