package com.ivy.disclaimer

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.ivy.disclaimer.composables.DisclaimerTopAppBar
import com.ivy.navigation.screenScopedViewModel

@Composable
fun DisclaimerScreenImpl() {
    val viewModel: DisclaimerViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerScreenUi(
    uiState: DisclaimerViewState,
    onEvent: (DisclaimerViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DisclaimerTopAppBar()
        },
        content = { innerPadding ->
            Content(modifier = Modifier.padding(innerPadding))
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        // Your content goes here
    }
}