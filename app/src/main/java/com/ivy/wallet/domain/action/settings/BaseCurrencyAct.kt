package com.ivy.wallet.domain.action.settings

import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.fp.wallet.baseCurrencyCode
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, String>() {
    override suspend fun Unit.recipe(): suspend () -> String {
        return suspend {
            io { baseCurrencyCode(settingsDao) }
        }
    }
}