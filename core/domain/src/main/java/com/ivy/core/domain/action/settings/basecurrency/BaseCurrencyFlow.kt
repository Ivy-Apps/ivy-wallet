package com.ivy.core.domain.action.settings.basecurrency

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
) : SharedFlowAction<CurrencyCode>() {
    override fun initialValue(): CurrencyCode = ""

    override fun createFlow(): Flow<CurrencyCode> =
        dataStore.get(settingsKeys.baseCurrency).filterNotNull()
}