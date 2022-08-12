package com.ivy.core.action.currency

import com.ivy.core.functions.getDefaultCurrencyCode
import com.ivy.data.CurrencyCode
import com.ivy.frp.action.FPAction
import com.ivy.state.baseCurrencyUpdate
import com.ivy.state.readIvyState
import com.ivy.state.writeIvyState
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class BaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<Unit, CurrencyCode>() {
    override suspend fun Unit.compose(): suspend () -> CurrencyCode = {
        readIvyState().baseCurrency ?: loadBaseCurrency().also {
            writeIvyState(baseCurrencyUpdate(it))
        }
    }

    private suspend fun loadBaseCurrency(): CurrencyCode =
        settingsDao.findAll().firstOrNull()?.currency ?: getDefaultCurrencyCode()

}