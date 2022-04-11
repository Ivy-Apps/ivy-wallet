package com.ivy.wallet.domain.action

import com.ivy.wallet.domain.fp.wallet.baseCurrencyCode
import com.ivy.wallet.io.persistence.dao.SettingsDao
import javax.inject.Inject

class GetBaseCurrencyAct @Inject constructor(
    private val settingsDao: SettingsDao
) : Action<Unit, String>() {
    override suspend fun Unit.willDo(): String = io {
        baseCurrencyCode(settingsDao)
    }
}