package com.ivy.home

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

//import com.ivy.core.ui.transaction.TrnsLazyColumn

@Composable
fun BoxScope.HomeTab() {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

//    uiState.trnsList.TrnsLazyColumn(scrollStateKey = "home")
}