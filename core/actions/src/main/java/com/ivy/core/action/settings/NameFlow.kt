package com.ivy.core.action.settings

import com.ivy.core.action.FlowAction
import com.ivy.wallet.io.persistence.dao.SettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NameFlow @Inject constructor(
    private val settingsDao: SettingsDao
) : FlowAction<Unit, String>() {
    override fun Unit.createFlow(): Flow<String> =
        settingsDao.findFirst().map {
            it.name
        }
}