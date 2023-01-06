package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.account.validateAccount
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.query.TrnQueryExecutor
import com.ivy.core.persistence.query.TrnWhere
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import javax.inject.Inject

/**
 * Persists _(saves or deletes)_ accounts locally. See [Modify].
 *
 * Use [Modify.save], [Modify.saveMany], [Modify.delete] or [Modify.deleteMany].
 */
class WriteAccountsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val writeTrnsAct: WriteTrnsAct,
    private val trnQueryExecutor: TrnQueryExecutor,
) : Action<Modify<Account>, Unit>() {

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
        writeTrnsAct(Modify.deleteMany(trns.map { it.id }))
    }

    private suspend fun save(accounts: List<Account>) {
        val entities = accounts.filter(::validateAccount)
            .map {
                it.copy(
                    name = it.name.trim(),
                )
            }
            .map {
                mapToEntity(it).copy(sync = SyncState.Syncing)
            }
        accountDao.save(entities)
    }
}