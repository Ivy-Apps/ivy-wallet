package com.ivy.menu

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class HomeMoreMenuViewModel @Inject constructor(
    private val navigator: Navigator,
) : SimpleFlowViewModel<MoreMenuState, MoreMenuEvent>() {
    override val initialUi = MoreMenuState()

    override val uiFlow: Flow<MoreMenuState> = flowOf(initialUi)


    // region Event Handling
    override suspend fun handleEvent(event: MoreMenuEvent) = when (event) {
        MoreMenuEvent.CategoriesClick -> handleCategoriesClick()
        MoreMenuEvent.SettingsClick -> handleSettingsClick()
    }

    private fun handleCategoriesClick() {
        navigator.navigate(Destination.categories.destination(Unit))
    }

    private fun handleSettingsClick() {
        navigator.navigate(Destination.settings.destination(Unit))
    }
    // endregion
}