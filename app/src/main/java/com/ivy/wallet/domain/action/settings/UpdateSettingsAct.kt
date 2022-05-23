package com.ivy.wallet.domain.action.settings

import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Settings
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        settingsDao.save(this.toEntity())
        this
    }
}