package com.ivy.core.action.account

import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.state.accountsUpdate
import com.ivy.state.invalidate
import com.ivy.state.writeIvyState
import com.ivy.sync.SyncTask
import com.ivy.sync.account.SyncAccountsAct
import com.ivy.sync.account.mark
import com.ivy.sync.syncTaskFrom
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteAccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val syncAccountsAct: SyncAccountsAct
) : FPAction<IOEffect<List<Account>>, SyncTask>() {
    override suspend fun IOEffect<List<Account>>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Delete -> item.forEach { delete(it) }
            is IOEffect.Save -> item.forEach { save(it) }
        }

        // Invalidate cache
        writeIvyState(accountsUpdate(invalidate()))

        syncTaskFrom(this asParamTo syncAccountsAct)
    }

    private suspend fun delete(account: Account) {
        transactionDao.flagDeletedByAccountId(accountId = account.id)
        persist(
            account.mark(
                isSynced = false,
                isDeleted = true
            )
        )
    }

    private suspend fun save(account: Account) {
        persist(
            account.mark(
                isSynced = false,
                isDeleted = false
            )
        )
    }

    private suspend fun persist(item: Account) =
        accountDao.save(mapToEntity(item))
}