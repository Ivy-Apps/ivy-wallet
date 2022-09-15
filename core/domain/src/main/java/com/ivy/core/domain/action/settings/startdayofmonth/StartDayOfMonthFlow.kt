package com.ivy.core.domain.action.settings.startdayofmonth

import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartDayOfMonthFlow @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
) : SharedFlowAction<Int>() {
    override fun initialValue(): Int = 1

    override fun createFlow(): Flow<Int> =
        dataStore.get(key = settingsKeys.startDayOfMonth)
            .map { it ?: initialValue() }
}