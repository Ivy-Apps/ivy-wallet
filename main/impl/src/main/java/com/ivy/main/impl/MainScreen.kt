package com.ivy.main.impl


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
import com.ivy.accounts.AccountTab
import com.ivy.accounts.AccountTabEvent
import com.ivy.accounts.AccountTabViewModel
import com.ivy.design.l2_components.modal.openModals
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.ScreenPlaceholder
import com.ivy.design.util.isInPreview
import com.ivy.home.HomeEvent
import com.ivy.home.HomeTab
import com.ivy.home.HomeViewModel
import com.ivy.main.base.MainBottomBarAction
import com.ivy.main.impl.components.MainBottomBar
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
    val accountViewModel: AccountTabViewModel = hiltViewModel()
    UI(
        selectedTab = state.selectedTab,
        bottomBarVisible = state.bottomBarVisible,
        onEvent = viewModel::onEvent,
        onHomeTabEvent = homeViewModel::onEvent,
        onAccountTabEvent = accountViewModel::onEvent,
    )
}

@Composable
private fun UI(
    selectedTab: Tab,
    bottomBarVisible: Boolean,
    onEvent: (MainEvent) -> Unit,
    onHomeTabEvent: (HomeEvent) -> Unit,
    onAccountTabEvent: (AccountTabEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalSwipeListener(
                sensitivity = 200,
                onSwipeLeft = { switchTabs(onEvent) },
                onSwipeRight = { switchTabs(onEvent) }
            )
    ) {
        when (selectedTab) {
            Tab.Home -> HomePreviewSafeTab()
            Tab.Accounts -> AccountPreviewSafeTab()
        }

        // region Bottom bar
        MainBottomBar(
            visible = bottomBarVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .systemBarsPadding(),
            selectedTab = selectedTab,
            onActionClick = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(MainBottomBarAction.Click),
                    accountEvent = accountEvent(MainBottomBarAction.Click),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeUp = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(MainBottomBarAction.SwipeUp),
                    accountEvent = accountEvent(MainBottomBarAction.SwipeUp),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeDiagonalLeft = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(MainBottomBarAction.SwipeDiagonalLeft),
                    accountEvent = accountEvent(MainBottomBarAction.SwipeDiagonalLeft),
                    selectedTab = selectedTab,
                    onHomeTabEvent = onHomeTabEvent,
                    onAccountTabEvent = onAccountTabEvent
                )
            },
            onActionSwipeDiagonalRight = {
                propagateBottomActionEvent(
                    homeEvent = homeEvent(MainBottomBarAction.SwipeDiagonalRight),
                    accountEvent = accountEvent(MainBottomBarAction.SwipeDiagonalRight),
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

private fun switchTabs(onEvent: (MainEvent) -> Unit) {
    if (openModals <= 0) {
        onEvent(MainEvent.SwitchSelectedTab)
    }
}

// region Bottom Action Bar events propagation
private fun propagateBottomActionEvent(
    homeEvent: HomeEvent,
    accountEvent: AccountTabEvent,
    selectedTab: Tab,
    onHomeTabEvent: (HomeEvent) -> Unit,
    onAccountTabEvent: (AccountTabEvent) -> Unit
) {
    when (selectedTab) {
        Tab.Home -> onHomeTabEvent(homeEvent)
        Tab.Accounts -> onAccountTabEvent(accountEvent)
    }
}

private fun homeEvent(action: MainBottomBarAction): HomeEvent =
    HomeEvent.BottomBarAction(action)

private fun accountEvent(action: MainBottomBarAction): AccountTabEvent =
    AccountTabEvent.BottomBarAction(action)
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
            bottomBarVisible = true,
            onEvent = {},
            onHomeTabEvent = {},
            onAccountTabEvent = {}
        )
    }
}

@Preview
@Composable
private fun Preview_BottomBar_hidden() {
    IvyPreview {
        UI(
            selectedTab = Tab.Home,
            bottomBarVisible = false,
            onEvent = {},
            onHomeTabEvent = {},
            onAccountTabEvent = {}
        )
    }
}
// endregion
