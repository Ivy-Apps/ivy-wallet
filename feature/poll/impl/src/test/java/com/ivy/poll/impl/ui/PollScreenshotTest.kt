package com.ivy.poll.impl.ui

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.ui.testing.PaparazziScreenshotTest
import com.ivy.ui.testing.PaparazziTheme
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class PollScreenshotTest(
  @TestParameter
  private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
  @Test
  fun `poll state`() {
    snapshot(theme) {
      PollUi(
        uiState = PollUiState.Content(
          poll = PollUi(
            title = "How much are you willing to pay for Ivy Wallet?",
            description = "To continue to exist, Ivy Wallet needs maintenance." +
                " Updating it requires effort and we can't do it for free.",
            options = persistentListOf(
              "$1/month + taxes \"as-is\" for maintenance",
              "$5/month + taxes for new features (e.g. google drive sync, AI, etc)",
              "None, I'll uninstall",
            )
          ),
          voteLoading = false,
          voteEnabled = true,
          selectedIndex = 1,
        ),
        onEvent = {}
      )
    }
  }

  @Test
  fun `voted state`() {
    snapshot(theme) {
      PollUi(
        uiState = PollUiState.Voted,
        onEvent = {}
      )
    }
  }
}