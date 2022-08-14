package com.ivy.sync.account

import com.ivy.data.SyncMetadata
import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.sync.base.SyncItem
import com.ivy.sync.ivyserver.account.AccountIvyServerSync
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class SyncAccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val ivyServerSync: AccountIvyServerSync
) : FPAction<IOEffect<List<Account>>, Unit>() {
    override suspend fun IOEffect<List<Account>>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<List<Account>>) {
        val sync = ivyServerSync.enabled() ?: return

        when (operation) {
            is IOEffect.Delete -> delete(sync = sync, items = operation.item)
            is IOEffect.Save -> sync.save(operation.item)
        }
    }

    private suspend fun delete(sync: SyncItem<Account>, items: List<Account>) {
        sync.delete(items)
        // delete all locally not matter the result
        items.forEach {
            transactionDao.deleteAllByAccountId(accountId = it.id)
            accountDao.deleteById(it.id)
        }
    }

    private suspend fun save(sync: SyncItem<Account>, items: List<Account>) =
        sync.save(items).map {
            val syncedItem = it.mark(
                isSynced = true, isDeleted = false
            )
            persist(syncedItem)
        }

    private suspend fun persist(item: Account) {
        accountDao.save(mapToEntity(item))
    }
}

fun Account.mark(
    isSynced: Boolean,
    isDeleted: Boolean
): Account = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = isDeleted,
        )
    )
)