package com.ivy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivy.core.domain.FlowViewModel

@Composable
inline fun <reified InternalState, reified UiState, reified Event> uiStatePreviewSafe(
    viewModel: FlowViewModel<InternalState, UiState, Event>?,
    preview: () -> UiState
): UiState = viewModel?.uiState?.collectAsState()?.value ?: preview()