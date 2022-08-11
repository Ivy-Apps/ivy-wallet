package com.ivy.core.action.account

import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.state.accountsUpdate
import com.ivy.state.invalidate
import com.ivy.state.writeIvyState
import com.ivy.sync.SyncTask
import com.ivy.sync.account.SyncAccountAct
import com.ivy.sync.account.mark
import com.ivy.sync.syncTaskFrom
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteAccountAct @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val syncAccountAct: SyncAccountAct
) : FPAction<IOEffect<Account>, SyncTask>() {
    override suspend fun IOEffect<Account>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Delete -> delete(item)
            is IOEffect.Save -> persist(
                item.mark(
                    isSynced = false,
                    isDeleted = false
                )
            )
        }

        // Invalidate cache
        writeIvyState(accountsUpdate(invalidate()))

        syncTaskFrom(this asParamTo syncAccountAct)
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

    private suspend fun persist(acc: Account) = accountDao.save(mapToEntity(acc))
}