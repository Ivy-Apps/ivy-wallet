package com.ivy.main.impl

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.main.base.MainBottomBarVisibility
import com.ivy.navigation.destinations.main.Main.Tab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    bottomBarVisibility: MainBottomBarVisibility,
) : SimpleFlowViewModel<MainState, MainEvent>() {
    override val initialUi: MainState = MainState(
        selectedTab = Tab.Home,
        bottomBarVisible = true,
    )

    private val selectedTab = MutableStateFlow(Tab.Home)

    override val uiFlow: Flow<MainState> = combine(
        selectedTab, bottomBarVisibility.visible
    ) { selectedTab, bottomBarVisible ->
        MainState(
            selectedTab = selectedTab,
            bottomBarVisible = bottomBarVisible,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: MainEvent) = when (event) {
        is MainEvent.SelectTab -> selectTab(event)
        MainEvent.SwitchSelectedTab -> toggleTabs()
    }

    private fun selectTab(event: MainEvent.SelectTab) {
        if (event.tab != null) {
            selectedTab.value = event.tab
        } else {
            // we can introduce different logic in the future
            selectedTab.value = Tab.Home
        }
    }

    private fun toggleTabs() {
        selectedTab.value = when (uiState.value.selectedTab) {
            Tab.Home -> Tab.Accounts
            Tab.Accounts -> Tab.Home
        }
    }
    // endregion
}