package com.ivy.core.action.account

import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.action.icon.DefaultTo
import com.ivy.core.action.icon.IconAct
import com.ivy.data.SyncMetadata
import com.ivy.data.account.AccMetadata
import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.accountsUpdate
import com.ivy.state.writeIvyState
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val iconAct: IconAct,
) : FPAction<Unit, List<Account>>() {
    override suspend fun Unit.compose(): suspend () -> List<Account> = {
        // TODO: enable caching
        // readIvyState().accounts ?: loadAccounts()
        loadAccounts()
    }

    private suspend fun loadAccounts(): List<Account> = accountDao::findAll then { entities ->
        val baseCurrency = baseCurrencyAct(Unit)
        entities.map {
            Account(
                id = it.id,
                name = it.name,
                currency = it.currency ?: baseCurrency,
                color = it.color,
                icon = iconAct(
                    IconAct.Input(
                        iconId = it.icon,
                        defaultTo = DefaultTo.Account
                    )
                ),
                excluded = !it.includeInBalance,
                metadata = AccMetadata(
                    orderNum = it.orderNum,
                    sync = SyncMetadata(
                        isSynced = it.isSynced,
                        isDeleted = it.isDeleted
                    )
                )
            )
        }
    } thenInvokeAfter { accounts ->
        writeIvyState(accountsUpdate(newAccounts = accounts))
        accounts
    }
}