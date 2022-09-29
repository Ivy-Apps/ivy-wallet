package com.ivy.main


import AccountTab
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.accounts.AccBottomBarAction
import com.ivy.accounts.AccountEvent
import com.ivy.accounts.AccountViewModel
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.ScreenPlaceholder
import com.ivy.design.util.isInPreview
import com.ivy.home.HomeTab
import com.ivy.home.HomeViewModel
import com.ivy.home.event.HomeBottomBarAction
import com.ivy.home.event.HomeEvent
import com.ivy.main.components.BottomBar
import com.ivy.navigation.destinations.main.Main.Tab
import com.ivy.wallet.utils.horizontalSwipeListener

@Composable
fun MainScreen(tab: Tab?) {
    val viewModel: MainViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(tab) {
        viewModel.onEvent(MainEvent.SelectTab(tab))
    }

    val homeViewModel: HomeViewModel = hiltViewModel()
    val accountViewModel: AccountViewModel = hiltViewModel()
    UI(
        selectedTab = state.selectedTab,
        onEvent = viewModel::onEvent,
        onHomeTabEvent = homeViewModel::onEvent,
        onAccountTabEvent = accountViewModel::onEvent,
    )
}

@Composable
private fun UI(
    selectedTab: Tab,
    onEvent: (MainEvent) -> Unit,
    onHomeTabEvent: (HomeEvent) -> Unit,
    onAccountTabEvent: (AccountEvent) -> Unit,
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
            Tab.Accounts -> AccountPreviewSafeTab()
        }

        // region Bottom bar
        BottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .systemBarsPadding(),
            selectedTab = selectedTab,
            onActionClick = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(HomeBottomBarAction.Click),
                    accountEvent = accountEvent(AccBottomBarAction.Click),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeUp = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(HomeBottomBarAction.SwipeUp),
                    accountEvent = accountEvent(AccBottomBarAction.SwipeUp),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeDiagonalLeft = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(HomeBottomBarAction.SwipeDiagonalLeft),
                    accountEvent = accountEvent(AccBottomBarAction.SwipeDiagonalLeft),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeDiagonalRight = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(HomeBottomBarAction.SwipeDiagonalRight),
                    accountEvent = accountEvent(AccBottomBarAction.SwipeDiagonalRight),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onHomeClick = { onEvent(MainEvent.SelectTab(Tab.Home)) },
            onAccountsClick = { onEvent(MainEvent.SelectTab(Tab.Accounts)) }
        )
        // endregion
    }
}

// region Bottom Action Bar events propagation
private fun propagateBottomActionEvent(
    homeEvent: HomeEvent,
    accountEvent: AccountEvent,
    selectedTab: Tab,
    onHomeTabEvent: (HomeEvent) -> Unit,
    onAccountTabEvent: (AccountEvent) -> Unit
) {
    when (selectedTab) {
        Tab.Home -> onHomeTabEvent(homeEvent)
        Tab.Accounts -> onAccountTabEvent(accountEvent)
    }
}

private fun homeEvent(action: HomeBottomBarAction): HomeEvent =
    HomeEvent.BottomBarAction(action)

private fun accountEvent(action: AccBottomBarAction): AccountEvent =
    AccountEvent.BottomBarAction(action)
// endregion

// region Preview-safe Tabs
@Composable
private fun BoxScope.HomePreviewSafeTab() {
    PreviewSafeTab(text = "Home") {
        HomeTab()
    }
}

@Composable
private fun BoxScope.AccountPreviewSafeTab() {
    PreviewSafeTab(text = "Accounts") {
        AccountTab()
    }
}

@Composable
private fun BoxScope.PreviewSafeTab(
    text: String,
    realTab: @Composable BoxScope.() -> Unit
) {
    if (isInPreview()) {
        ScreenPlaceholder(text = text)
    } else {
        realTab()
    }
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            selectedTab = Tab.Home,
            onEvent = {},
            onHomeTabEvent = {},
            onAccountTabEvent = {}
        )
    }
}
// endregion
