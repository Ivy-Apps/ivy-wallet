package com.ivy.core.domain.action.settings.theme

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import com.ivy.data.Theme
import javax.inject.Inject

class WriteThemeAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : Action<Theme, Unit>() {

    override suspend fun Theme.willDo() {
        dataStore.put(key = settingsKeys.theme, this.code)
    }
}