package com.ivy.core.domain.action.settings.basecurrency

import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import com.ivy.frp.action.Action
import javax.inject.Inject

class WriteBaseCurrencyAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys
) : Action<String, Unit>() {
    override suspend fun String.willDo() {
        dataStore.put(key = settingsKeys.baseCurrency, value = this)
    }
}