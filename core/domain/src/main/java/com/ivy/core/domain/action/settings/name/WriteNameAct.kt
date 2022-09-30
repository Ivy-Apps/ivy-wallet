package com.ivy.core.domain.action.settings.name

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import javax.inject.Inject

class WriteNameAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : Action<String, Unit>() {
    override suspend fun String.willDo() {
        dataStore.put(settingsKeys.displayName, this)
    }
}