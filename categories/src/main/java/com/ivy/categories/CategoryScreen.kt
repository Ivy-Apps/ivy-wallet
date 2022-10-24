package com.ivy.categories

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.CategoryScreen() {
    val viewModel: CategoryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
}

@Composable
private fun UI(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {

}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = CategoryState(
                items = emptyList(),
            ),
            onEvent = {}
        )
    }
}
// endregion