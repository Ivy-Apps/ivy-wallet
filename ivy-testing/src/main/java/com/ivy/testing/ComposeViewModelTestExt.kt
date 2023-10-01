package com.ivy.testing

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.ivy.domain.ComposeViewModel

/**
 * Runs a [ComposeViewModel] test simulation.
 * Compose runtime effects are executed [RecompositionMode.Immediate].
 * @param events pass the events that have occurred in your simulation
 * @param verify assert what's the expected state after all the events
 */
fun <UiState, UiEvent> ComposeViewModel<UiState, UiEvent>.runTest(
    vararg events: UiEvent,
    verify: UiState.() -> Unit
) {
    val viewModel = this
    kotlinx.coroutines.test.runTest {
        moleculeFlow(mode = RecompositionMode.Immediate) {
            viewModel.uiState()
        }.test {
            events.onEach(viewModel::onEvent)
            verify(expectMostRecentItem())
            cancel()
        }
    }
}