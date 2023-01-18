package com.ivy.menu

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.settings.theme.ThemeFlow
import com.ivy.core.domain.action.settings.theme.WriteThemeAct
import com.ivy.data.Theme
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeMoreMenuViewModel @Inject constructor(
    private val navigator: Navigator,
    private val themeFlow: ThemeFlow,
    private val writeThemeAct: WriteThemeAct,
) : SimpleFlowViewModel<MoreMenuState, MoreMenuEvent>() {
    override val initialUi = MoreMenuState(
        theme = Theme.Auto
    )

    override val uiFlow: Flow<MoreMenuState> = themeFlow(Unit).map { theme ->
        MoreMenuState(
            theme = theme
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: MoreMenuEvent) = when (event) {
        MoreMenuEvent.CategoriesClick -> handleCategoriesClick()
        MoreMenuEvent.SettingsClick -> handleSettingsClick()
        is MoreMenuEvent.ThemeChange -> handleThemeChange(event)
        MoreMenuEvent.Close -> navigator.back()
    }

    private fun handleCategoriesClick() {
        navigator.navigate(Destination.categories.destination(Unit))
    }

    private fun handleSettingsClick() {
        navigator.navigate(Destination.settings.destination(Unit))
    }

    private suspend fun handleThemeChange(event: MoreMenuEvent.ThemeChange) {
        writeThemeAct(event.theme)
    }
    // endregion
}