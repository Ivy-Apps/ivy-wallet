package com.ivy.core.action.time

import com.ivy.core.action.SharedFlowAction
import com.ivy.temp.persistence.datastore.IvyDataStoreKeys
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartDayOfMonthFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val dataStoreKeys: IvyDataStoreKeys,
) : SharedFlowAction<Int>() {
    override suspend fun initialValue(): Int = 1

    override suspend fun createFlow(): Flow<Int> =
        dataStore.get(key = dataStoreKeys.startDayOfMonth)
            .map {
                it ?: initialValue()
            }
}