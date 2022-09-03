package com.ivy.core.action.account

import com.ivy.core.action.SharedFlowAction
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.icon.DefaultTo
import com.ivy.core.action.icon.IconAct
import com.ivy.data.CurrencyCode
import com.ivy.data.SyncMetadata
import com.ivy.data.account.AccMetadata
import com.ivy.data.account.Account
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.data.AccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsFlow @Inject constructor(
    private val accountDao: AccountDao,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val iconAct: IconAct,
) : SharedFlowAction<List<Account>>() {

    override suspend fun initialValue(): List<Account> = emptyList()

    override suspend fun createFlow(): Flow<List<Account>> =
        combine(accountDao.findAll(), baseCurrencyFlow()) { entities, baseCurrency ->
            entities.map { toAccount(acc = it, baseCurrency = baseCurrency) }
        }.flowOn(Dispatchers.IO)

    private suspend fun toAccount(acc: AccountEntity, baseCurrency: CurrencyCode): Account =
        Account(
            id = acc.id,
            name = acc.name,
            currency = acc.currency ?: baseCurrency,
            color = acc.color,
            icon = iconAct(
                IconAct.Input(
                    iconId = acc.icon,
                    defaultTo = DefaultTo.Account
                )
            ),
            excluded = !acc.includeInBalance,
            metadata = AccMetadata(
                orderNum = acc.orderNum,
                sync = SyncMetadata(
                    isSynced = acc.isSynced,
                    isDeleted = acc.isDeleted
                )
            )
        )
}