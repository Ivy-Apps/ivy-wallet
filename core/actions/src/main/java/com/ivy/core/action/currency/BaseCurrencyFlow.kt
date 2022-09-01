package com.ivy.core.action.currency

import com.ivy.core.action.SharedFlowAction
import com.ivy.data.CurrencyCode
import com.ivy.wallet.io.persistence.dao.SettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyFlow @Inject constructor(
    private val settingsDao: SettingsDao
) : SharedFlowAction<CurrencyCode>() {
    override suspend fun createFlow(): Flow<CurrencyCode> =
        settingsDao.findFirstFlow()
            .map { it.currency }

    override suspend fun initialValue(): CurrencyCode = ""
}