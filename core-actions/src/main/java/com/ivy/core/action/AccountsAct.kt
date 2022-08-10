package com.ivy.core.action

import com.ivy.data.SyncMetadata
import com.ivy.data.account.AccMetadata
import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.accountsUpdate
import com.ivy.state.readIvyState
import com.ivy.state.writeIvyState
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<Unit, List<Account>>() {
    override suspend fun Unit.compose(): suspend () -> List<Account> = {
        readIvyState().accounts ?: loadAccounts()
    }

    private suspend fun loadAccounts(): List<Account> = accountDao::findAll then { entities ->
        val baseCurrency = baseCurrencyAct(Unit)
        entities.map {
            Account(
                name = it.name,
                currencyCode = it.currency ?: baseCurrency,
                color = it.color,
                icon = it.icon,
                id = it.id,
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