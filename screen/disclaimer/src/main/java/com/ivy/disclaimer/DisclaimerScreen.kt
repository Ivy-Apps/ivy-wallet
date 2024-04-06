package com.ivy.disclaimer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.disclaimer.composables.AcceptTermsText
import com.ivy.disclaimer.composables.AgreeButton
import com.ivy.disclaimer.composables.AgreementCheckBox
import com.ivy.disclaimer.composables.DisclaimerTopAppBar
import com.ivy.navigation.screenScopedViewModel
import com.ivy.ui.component.OpenSourceCard

@Composable
fun DisclaimerScreenImpl() {
    val viewModel: DisclaimerViewModel = screenScopedViewModel()
    val viewState = viewModel.uiState()
    DisclaimerScreenUi(viewState = viewState, onEvent = viewModel::onEvent)
}

@Composable
fun DisclaimerScreenUi(
    viewState: DisclaimerViewState,
    onEvent: (DisclaimerViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DisclaimerTopAppBar()
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier.padding(innerPadding),
                viewState = viewState,
                onEvent = onEvent,
            )
        }
    )
}

@Composable
private fun Content(
    viewState: DisclaimerViewState,
    onEvent: (DisclaimerViewEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            OpenSourceCard()
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AcceptTermsText()
        }
        itemsIndexed(items = viewState.checkboxes) { index, item ->
            Spacer(modifier = Modifier.height(8.dp))
            AgreementCheckBox(
                viewState = item,
                onClick = {
                    onEvent(DisclaimerViewEvent.OnCheckboxClick(index))
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            AgreeButton(
                enabled = viewState.agreeButtonEnabled,
            ) { onEvent(DisclaimerViewEvent.OnAgreeClick) }
        }
        item {
            // To ensure proper scrolling
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}