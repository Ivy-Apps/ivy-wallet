package com.ivy.core.domain.action.settings.applocked

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import javax.inject.Inject

class WriteAppLockedAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : Action<Boolean, Unit>() {
    override suspend fun Boolean.willDo() {
        dataStore.put(key = settingsKeys.appLocked, this)
    }
}