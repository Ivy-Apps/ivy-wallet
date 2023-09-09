package com.ivy.wallet.domain.action.settings

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, String>() {
    override suspend fun Unit.compose(): suspend () -> String = suspend {
        io { settingsDao.findFirst().currency }
    }
}
