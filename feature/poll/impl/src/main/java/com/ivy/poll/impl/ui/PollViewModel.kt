package com.ivy.poll.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ivy.poll.data.model.Poll
import com.ivy.poll.data.model.PollId
import com.ivy.poll.data.model.PollOption
import com.ivy.poll.data.model.PollOptionId
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@Stable
@HiltViewModel
class PollViewModel @Inject constructor() : ComposeViewModel<PollUiState, PollUiEvent>() {
  private var selectedIndex by mutableStateOf<Int?>(null)

  private val poll = Poll(
    id = PollId.PaidIvy,
    title = "How much are you willing to pay for Ivy Wallet?",
    options = listOf(
      PollOption(
        id = PollOptionId("one_usd"),
        text = "$1/month + taxes \"as-is\" for maintenance",
      ),
      PollOption(
        id = PollOptionId("five_usd"),
        text = "$5/month + taxes for new features (e.g. google drive sync, AI, etc)",
      ),
      PollOption(
        id = PollOptionId("none"),
        text = "None, I'll uninstall",
      ),
    )
  )

  @Composable
  override fun uiState(): PollUiState {
    return PollUiState(
      poll = getPoll(),
      selectedIndex = selectedIndex,
      voteEnabled = selectedIndex != null,
    )
  }

  @Composable
  private fun getPoll(): PollUi {
    return remember(poll) {
      PollUi(
        title = poll.title,
        options = poll.options.map { it.text }.toImmutableList(),
      )
    }
  }

  override fun onEvent(event: PollUiEvent) {
    when (event) {
      is PollUiEvent.SelectOption -> {
        selectedIndex = event.index
      }

      PollUiEvent.VoteClick -> TODO()
    }
  }
}