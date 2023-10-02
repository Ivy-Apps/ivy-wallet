package com.ivy.wallet.domain.action.settings

import com.ivy.legacy.datamodel.Settings
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.data.db.dao.read.SettingsDao
import javax.inject.Inject

class SettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, Settings>() {
    override suspend fun Unit.compose(): suspend () -> Settings = suspend {
        io { settingsDao.findFirst() }
    } then { it.toDomain() }
}
