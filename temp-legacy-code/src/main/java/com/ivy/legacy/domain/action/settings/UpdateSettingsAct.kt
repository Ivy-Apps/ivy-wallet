package com.ivy.legacy.domain.action.settings

import com.ivy.core.db.write.SettingsWriter
import com.ivy.core.datamodel.Settings
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class UpdateSettingsAct @Inject constructor(
    private val settingsWriter: SettingsWriter
) : FPAction<Settings, Settings>() {
    override suspend fun Settings.compose(): suspend () -> Settings = suspend {
        settingsWriter.save(this.toEntity())
        this
    }
}
