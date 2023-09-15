package com.ivy.wallet.domain.action.settings

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.core.data.model.Settings
import com.ivy.core.data.db.dao.SettingsDao
import javax.inject.Inject

class SettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, Settings>() {
    override suspend fun Unit.compose(): suspend () -> Settings = suspend {
        io { settingsDao.findFirst() }
    } then { it.toDomain() }
}
