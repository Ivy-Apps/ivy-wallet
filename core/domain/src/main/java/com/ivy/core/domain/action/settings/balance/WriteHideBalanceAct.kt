package com.ivy.core.domain.action.settings.balance

import com.ivy.frp.action.Action
import com.ivy.temp.persistence.datastore.IvyDataStoreKeys
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import javax.inject.Inject

class WriteHideBalanceAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val dataStoreKeys: IvyDataStoreKeys
) : Action<Boolean, Unit>() {

    override suspend fun Boolean.willDo() {
        dataStore.put(key = dataStoreKeys.hideBalance, this)
    }
}