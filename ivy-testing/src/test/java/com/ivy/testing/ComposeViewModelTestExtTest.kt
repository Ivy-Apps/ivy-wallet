package com.ivy.testing

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComposeViewModelTestExtTest : FreeSpec({
    "initial state, no events" {
        val viewModel = FakeViewModel()

        viewModel.runTest(
            events = {}
        ) {
            awaitItem().counter shouldBe 0
            awaitItem().counter shouldBe 42
        }
    }

    "increment event" {
        val viewModel = FakeViewModel()

        viewModel.runTest(
            events = {
                onEvent(FakeUiEvent.Increment)
            }
        ) {
            expectMostRecentItem().counter shouldBe 43
        }
    }
})