package com.ivy.poll.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.ivy.base.Toaster
import com.ivy.navigation.Navigation
import com.ivy.poll.data.model.Poll
import com.ivy.poll.data.model.PollId
import com.ivy.poll.data.model.PollOption
import com.ivy.poll.data.model.PollOptionId
import com.ivy.poll.impl.domain.VoteUseCase
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class PollViewModel @Inject constructor(
  private val voteUseCase: VoteUseCase,
  private val toaster: Toaster,
  private val navigation: Navigation,
) : ComposeViewModel<PollUiState, PollUiEvent>() {
  private var selectedIndex by mutableStateOf<Int?>(null)
  private var voteLoading by mutableStateOf(false)
  private var voted by mutableStateOf(false)

  private val poll = Poll(
    id = PollId.PaidIvy,
    title = "How much are you willing to pay for Ivy Wallet?",
    description = "To continue to exist, Ivy Wallet needs maintenance." +
        " Updating it requires effort and we can't do it for free.",
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
        text = "None, I'll uninstall.",
      ),
    )
  )

  @Composable
  override fun uiState(): PollUiState {
    return when {
      voted -> PollUiState.Voted
      else -> PollUiState.Content(
        poll = getPoll(),
        selectedIndex = selectedIndex,
        voteEnabled = selectedIndex != null,
        voteLoading = voteLoading,
      )
    }
  }

  @Composable
  private fun getPoll(): PollUi {
    return remember(poll) {
      PollUi(
        title = poll.title,
        description = poll.description,
        options = poll.options.map { it.text }.toImmutableList(),
      )
    }
  }

  override fun onEvent(event: PollUiEvent) {
    when (event) {
      is PollUiEvent.SelectOption -> {
        selectedIndex = event.index
      }

      PollUiEvent.VoteClick -> handleVoteClick()
      PollUiEvent.BackClick -> {
        navigation.back()
      }
    }
  }

  private fun handleVoteClick() {
    val selectedIndex = selectedIndex
    checkNotNull(selectedIndex) {
      "Poll: Attempting to vote without selecting an option first"
    }

    viewModelScope.launch {
      voteLoading = true
      voteUseCase.vote(
        poll = poll.id,
        option = poll.options[selectedIndex].id,
      ).onLeft {
        toaster.show(message = "Error: $it")
      }.onRight {
        voted = true
      }
      voteLoading = false
    }
  }
}