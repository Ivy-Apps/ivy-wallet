package com.ivy.testing

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.ivy.domain.ComposeViewModel
import kotlinx.coroutines.test.runTest

fun <UiState, UiEvent> ComposeViewModel<UiState, UiEvent>.runTest(
    events: ComposeViewModel<UiState, UiEvent>.() -> Unit,
    validate: suspend TurbineTestContext<UiState>.() -> Unit
) {
    val viewModel = this
    runTest {
        moleculeFlow(mode = RecompositionMode.Immediate) {
            val uiState = viewModel.uiState()
            uiState
        }.test {
            viewModel.events()
            validate(this)
            cancel()
        }
    }
}