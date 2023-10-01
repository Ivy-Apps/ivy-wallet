package com.ivy.testing

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComposeViewModelTestExtTest : FreeSpec({
    "initial state, no events" {
        val viewModel = FakeViewModel()

        viewModel.runTest {
            counter shouldBe 42
        }
    }

    "increment event" {
        val viewModel = FakeViewModel()

        viewModel.runTest(
            events = {
                onEvent(FakeUiEvent.Increment)
            }
        ) {
            counter shouldBe 43
        }
    }

    "decrement event" {
        val viewModel = FakeViewModel()

        viewModel.runTest(
            events = {
                onEvent(FakeUiEvent.Decrement)
            }
        ) {
            counter shouldBe 41
        }
    }

    "1 decrement 2 increment event" {
        val viewModel = FakeViewModel()

        viewModel.runTest(
            events = {
                onEvent(FakeUiEvent.Decrement)
                onEvent(FakeUiEvent.Increment)
                onEvent(FakeUiEvent.Increment)
            }
        ) {
            counter shouldBe 43
        }
    }
})