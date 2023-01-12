package com.ivy.core.domain.action.settings.balance

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import javax.inject.Inject

class WriteHideBalanceAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : Action<Boolean, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(hideBalance: Boolean) {
        dataStore.put(key = settingsKeys.hideBalance, hideBalance)
    }
}