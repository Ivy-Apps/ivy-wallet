package com.ivy.testing

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ComposeViewModelTestExtTest : FreeSpec({
    fun newViewModel() = FakeViewModel()

    "initial state, no events" {
        val viewModel = newViewModel()

        viewModel.runTest {
            counter shouldBe 42
        }
    }

    "increment event" {
        val viewModel = newViewModel()

        viewModel.runTest(
            FakeUiEvent.Increment
        ) {
            counter shouldBe 43
        }
    }

    "decrement event" {
        val viewModel = newViewModel()

        viewModel.runTest(
            FakeUiEvent.Decrement
        ) {
            counter shouldBe 41
        }
    }

    "1 decrement 2 increment event" {
        val viewModel = newViewModel()

        viewModel.runTest(
            FakeUiEvent.Decrement,
            FakeUiEvent.Increment,
            FakeUiEvent.Increment,
        ) {
            counter shouldBe 43
        }
    }
})