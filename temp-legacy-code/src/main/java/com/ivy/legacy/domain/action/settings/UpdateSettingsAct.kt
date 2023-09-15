package com.ivy.wallet.domain.action.settings

import com.ivy.frp.action.FPAction
import com.ivy.core.data.model.Settings
import com.ivy.core.data.db.dao.SettingsDao
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        settingsDao.save(this.toEntity())
        this
    }
}
