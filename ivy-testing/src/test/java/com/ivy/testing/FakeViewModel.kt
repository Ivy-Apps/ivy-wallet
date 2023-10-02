package com.ivy.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.ivy.domain.ComposeViewModel

data class FakeUiState(
    val counter: Int,
)

sealed interface FakeUiEvent {
    data object Increment : FakeUiEvent
    data object Decrement : FakeUiEvent
}

class FakeViewModel : ComposeViewModel<FakeUiState, FakeUiEvent>() {
    private var counter by mutableIntStateOf(0)

    @Composable
    override fun uiState(): FakeUiState {
        LaunchedEffect(Unit) {
            counter = 42
        }

        return FakeUiState(
            counter = counter,
        )
    }

    override fun onEvent(event: FakeUiEvent) {
        when (event) {
            FakeUiEvent.Decrement -> counter--
            FakeUiEvent.Increment -> counter++
        }
    }
}