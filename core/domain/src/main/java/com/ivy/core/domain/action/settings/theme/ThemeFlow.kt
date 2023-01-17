package com.ivy.core.domain.action.settings.theme

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import com.ivy.data.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : FlowAction<Unit, Theme>() {
    override fun createFlow(input: Unit): Flow<Theme> =
        dataStore.get(settingsKeys.theme)
            .map { it?.let(Theme::fromCode) ?: Theme.Auto }
}