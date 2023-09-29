package com.ivy.legacy.domain.action.settings

import com.ivy.legacy.datamodel.Settings
import com.ivy.frp.action.FPAction
import com.ivy.data.db.dao.write.WriteSettingsDao
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val writeSettingsDao: WriteSettingsDao
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        writeSettingsDao.save(this.toEntity())
        this
    }
}
