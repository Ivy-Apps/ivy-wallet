package com.ivy.core.domain.action.settings.startdayofmonth

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import javax.inject.Inject

class WriteStartDayOfMonthAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
) : Action<Int, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(startDayOfMonth: Int) {
        dataStore.put(key = settingsKeys.startDayOfMonth, value = startDayOfMonth)
    }
}