package com.ivy.core.action.settings.balance

import com.ivy.core.action.FlowAction
import com.ivy.temp.persistence.datastore.IvyDataStoreKeys
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HideBalanceSettingFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val dataStoreKeys: IvyDataStoreKeys
) : FlowAction<Unit, Boolean>() {
    override fun Unit.createFlow(): Flow<Boolean> =
        dataStore.get(dataStoreKeys.hideBalance).map {
            it ?: false
        }
}