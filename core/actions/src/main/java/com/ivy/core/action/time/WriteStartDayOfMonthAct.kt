package com.ivy.core.action.time

import com.ivy.frp.action.Action
import com.ivy.temp.persistence.datastore.IvyDataStoreKeys
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import javax.inject.Inject

class WriteStartDayOfMonthAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val dataStoreKeys: IvyDataStoreKeys,
) : Action<Int, Unit>() {
    override suspend fun Int.willDo() {
        dataStore.insert(key = dataStoreKeys.startDayOfMonth, value = this)
    }
}