package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.functions.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.query.TrnQuery
import com.ivy.core.persistence.query.TrnWhere
import com.ivy.data.Modify
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import javax.inject.Inject

class WriteAccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val writeTrnsAct: WriteTrnsAct,
    private val trnQuery: TrnQuery,
//    private val syncAccountsAct: SyncAccountsAct
) : FPAction<Modify<Account>, SyncTask>() {
    override suspend fun Modify<Account>.compose(): suspend () -> SyncTask = {
        when (this) {
            is Modify.Delete -> itemIds.forEach { delete(it) }
            is Modify.Save -> save(items)
        }

        // TODO: Implement sync
        syncTaskFrom {}
    }

    private suspend fun delete(accountId: String) {
        deleteTrns(accountId = accountId)
        // TODO: Delete planned payments associated with that accounts
        accountDao.updateSync(accountId = accountId, sync = SyncState.Deleting)
    }

    private suspend fun deleteTrns(accountId: String) {
        val trns = trnQuery.query(TrnWhere.ByAccountId(accountId))
        writeTrnsAct(Modify.Delete(trns.map { it.id }))
    }

    private suspend fun save(accounts: List<Account>) {
        val entities = accounts.map {
            mapToEntity(it).copy(sync = SyncState.Syncing)
        }
        accountDao.save(entities)
    }
}