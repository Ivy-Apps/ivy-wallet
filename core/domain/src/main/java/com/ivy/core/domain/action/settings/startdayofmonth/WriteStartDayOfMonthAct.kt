package com.ivy.core.domain.action.settings.startdayofmonth

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import javax.inject.Inject

class WriteStartDayOfMonthAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
) : Action<Int, Unit>() {
    override suspend fun Int.willDo() {
        dataStore.put(key = settingsKeys.startDayOfMonth, value = this)
    }
}