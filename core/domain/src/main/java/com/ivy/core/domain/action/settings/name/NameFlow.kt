package com.ivy.core.domain.action.settings.name

import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class NameFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : com.ivy.core.domain.action.FlowAction<Unit, String>() {
    override fun createFlow(input: Unit): Flow<String> =
        dataStore.get(settingsKeys.displayName)
            .filterNotNull()
}