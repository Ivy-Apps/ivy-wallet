package com.ivy.home

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.home.event.HomeEvent
import com.ivy.home.state.HomeStateUi

@Composable
fun BoxScope.HomeTab() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
}

@Composable
private fun BoxScope.UI(
    state: HomeStateUi,
    onEvent: (HomeEvent) -> Unit,
) {

}