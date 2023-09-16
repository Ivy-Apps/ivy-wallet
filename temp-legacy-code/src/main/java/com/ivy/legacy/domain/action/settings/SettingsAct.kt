package com.ivy.wallet.domain.action.settings

import com.ivy.core.data.db.read.SettingsDao
import com.ivy.core.data.model.Settings
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import javax.inject.Inject

class SettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, Settings>() {
    override suspend fun Unit.compose(): suspend () -> Settings = suspend {
        io { settingsDao.findFirst() }
    } then { it.toDomain() }
}
