package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.query.TrnQueryExecutor
import com.ivy.core.persistence.query.TrnWhere
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.frp.action.Action
import javax.inject.Inject

class WriteAccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val writeTrnsAct: WriteTrnsAct,
    private val trnQueryExecutor: TrnQueryExecutor,
) : Action<Modify<Account>, Unit>() {
    companion object {
        fun save(account: Account) = Modify.Save(listOf(account))
        fun saveMany(accounts: Iterable<Account>) = Modify.Save(accounts.toList())

        fun delete(accountId: String) = Modify.Delete<Account>(listOf(accountId))
        fun deleteMany(accIds: Iterable<String>) = Modify.Delete<Account>(accIds.toList())
    }

    override suspend fun Modify<Account>.willDo() {
        when (this) {
            is Modify.Delete -> itemIds.forEach { delete(it) }
            is Modify.Save -> save(items)
        }
    }

    private suspend fun delete(accountId: String) {
        deleteTrns(accountId = accountId)
        // TODO: Delete planned payments associated with that accounts
        accountDao.updateSync(accountId = accountId, sync = SyncState.Deleting)
    }

    private suspend fun deleteTrns(accountId: String) {
        val trns = trnQueryExecutor.query(TrnWhere.ByAccountId(accountId))
        writeTrnsAct(WriteTrnsAct.deleteMany(trns.map { it.id }))
    }

    private suspend fun save(accounts: List<Account>) {
        val entities = accounts.map {
            mapToEntity(it).copy(sync = SyncState.Syncing)
        }
        accountDao.save(entities)
    }
}