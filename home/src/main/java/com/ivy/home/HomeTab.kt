package com.ivy.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

//import com.ivy.core.ui.transaction.TrnsLazyColumn

@Composable
fun HomeTab() {
    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

//    uiState.trnsList.TrnsLazyColumn(scrollStateKey = "home")
}