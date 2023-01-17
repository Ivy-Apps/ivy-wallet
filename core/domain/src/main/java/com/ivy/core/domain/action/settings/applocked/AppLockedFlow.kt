package com.ivy.core.domain.action.settings.applocked

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppLockedFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : FlowAction<Unit, Boolean>() {
    override fun createFlow(input: Unit): Flow<Boolean> =
        dataStore.get(settingsKeys.appLocked)
            .map { it ?: false }
}