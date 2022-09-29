package com.ivy.main

import com.ivy.core.domain.FlowViewModel
import com.ivy.navigation.destinations.main.Main.Tab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : FlowViewModel<MainState, MainState, MainEvent>() {
    override fun initialState(): MainState = MainState(selectedTab = Tab.Home)

    override fun initialUiState(): MainState = initialState()

    private val selectedTab = MutableStateFlow(Tab.Home)

    override fun stateFlow(): Flow<MainState> = selectedTab.map {
        MainState(selectedTab = it)
    }

    override suspend fun mapToUiState(state: MainState) = state

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
        selectedTab.value = when (state.value.selectedTab) {
            Tab.Home -> Tab.Accounts
            Tab.Accounts -> Tab.Home
        }
    }

}