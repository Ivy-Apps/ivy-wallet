package com.ivy.testing

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class ComposeViewModelTestExtTest : ComposeViewModelTest() {

    private lateinit var viewModel: FakeViewModel

    @Before
    fun setup() {
        viewModel = FakeViewModel()
    }

    @Test
    fun `initial state, no events`() {
        viewModel.runTest {
            counter shouldBe 42
        }
    }

    @Test
    fun `increment event`() {
        viewModel.runTest(
            events = listOf(FakeUiEvent.Increment)
        ) {
            counter shouldBe 43
        }
    }

    @Test
    fun `decrement event`() {
        viewModel.runTest(
            events = listOf(FakeUiEvent.Decrement)
        ) {
            counter shouldBe 41
        }
    }

    @Test
    fun `1 decrement 2 increment event`() {
        viewModel.runTest(
            events = listOf(
                FakeUiEvent.Decrement,
                FakeUiEvent.Increment,
                FakeUiEvent.Increment,
            )
        ) {
            counter shouldBe 43
        }
    }
}
