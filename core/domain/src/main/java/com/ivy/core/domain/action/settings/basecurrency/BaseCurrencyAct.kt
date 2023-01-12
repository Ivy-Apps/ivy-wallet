package com.ivy.core.domain.action.settings.basecurrency

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
) : Action<Unit, CurrencyCode>() {

    override suspend fun action(input: Unit): CurrencyCode =
        dataStore.get(settingsKeys.baseCurrency).firstOrNull() ?: ""
}