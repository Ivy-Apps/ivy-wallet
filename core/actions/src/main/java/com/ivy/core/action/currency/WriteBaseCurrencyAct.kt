package com.ivy.core.action.currency

import com.ivy.frp.action.FPAction
import com.ivy.state.baseCurrencyUpdate
import com.ivy.state.exchangeRatesUpdate
import com.ivy.state.invalidate
import com.ivy.state.writeIvyState
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class WriteBaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : FPAction<String, Unit>() {
    override suspend fun String.compose(): suspend () -> Unit = {
        writeIvyState(baseCurrencyUpdate(newCurrency = this))
        writeIvyState(exchangeRatesUpdate(invalidate()))
        settingsDao.updateBaseCurrency(this)
    }
}