package com.ivy.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.H1
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.isInPreview
import com.ivy.home.HomeTab
import com.ivy.navigation.destinations.main.Main.Tab
import com.ivy.wallet.utils.horizontalSwipeListener

@Composable
fun MainScreen(tab: Tab?) {
    val viewModel: MainViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(tab) {
        viewModel.onEvent(MainEvent.SelectTab(tab))
    }
    UI(
        selectedTab = state.selectedTab,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun UI(
    selectedTab: Tab,
    onEvent: (MainEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalSwipeListener(
                sensitivity = 200,
                onSwipeLeft = { onEvent(MainEvent.SwitchSelectedTab) },
                onSwipeRight = { onEvent(MainEvent.SwitchSelectedTab) }
            )
    ) {
        when (selectedTab) {
            Tab.Home -> HomePreviewSafeTab()
            Tab.Accounts -> AccountsPreviewSafeTab()
        }

        BottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .systemBarsPadding(),
            selectedTab = selectedTab,
            onActionClick = {
                when (it) {
                    Tab.Home -> TODO()
                    Tab.Accounts -> TODO()
                }
            },
            onActionSwipeUp = {
                // TODO
            },
            onActionSwipeDiagonalLeft = {
                // TODO
            },
            onActionSwipeDiagonalRight = {
                // TODO
            },
            onHomeClick = { onEvent(MainEvent.SelectTab(Tab.Home)) },
            onAccountsClick = { onEvent(MainEvent.SelectTab(Tab.Accounts)) })
    }
}

@Composable
private fun BoxScope.HomePreviewSafeTab() {
    PreviewSafeTab(text = "Home") {
        HomeTab()
    }
}

@Composable
private fun BoxScope.AccountsPreviewSafeTab() {
    PreviewSafeTab(text = "Home") {
        HomeTab()
    }
}

@Composable
private fun BoxScope.PreviewSafeTab(
    text: String,
    realTab: @Composable BoxScope.() -> Unit
) {
    if (isInPreview()) {
        H1(
            modifier = Modifier
                .fillMaxSize()
                .background(UI.colors.pure),
            text = text,
            textAlign = TextAlign.Center
        )
    } else {
        realTab()
    }
}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(selectedTab = Tab.Home) {}
    }
}
// endregion
