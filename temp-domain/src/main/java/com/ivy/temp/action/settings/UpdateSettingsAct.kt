package com.ivy.wallet.domain.action.settings

import com.ivy.data.Settings
import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.data.toEntity
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        settingsDao.save(this.toEntity())
        this
    }
}